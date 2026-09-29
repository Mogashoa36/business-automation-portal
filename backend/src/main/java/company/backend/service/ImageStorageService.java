package company.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import company.backend.config.ImageStorageProperties;
import company.backend.dto.ImageResponse;
import company.backend.dto.ImageMetadataRequest;
import company.backend.entity.ContentImage;
import company.backend.exception.InvalidRequestException;
import company.backend.exception.ResourceNotFoundException;
import company.backend.repo.ContentImageRepository;

/**
 * Stores and serves images uploaded by administrators.
 *
 * <p>Uploads land in a directory configured outside the application's own
 * resources and are only ever read back through {@link #load}. Nothing maps the
 * storage directory to a static URL handler, so an uploaded file is never
 * executed or reinterpreted by the servlet container.
 *
 * <p>Every write name is server generated. A caller can influence nothing about
 * where a file is written, which removes path traversal as a class of bug
 * rather than merely filtering the obvious cases.
 */
@Service
public class ImageStorageService {

	private final ContentImageRepository imageRepository;
	private final ImageStorageProperties properties;
	private final Path storageRoot;

	public ImageStorageService(ContentImageRepository imageRepository, ImageStorageProperties properties) {
		this.imageRepository = imageRepository;
		this.properties = properties;
		this.storageRoot = Paths.get(properties.directory()).toAbsolutePath().normalize();
	}

	/**
	 * Validates and stores an upload.
	 *
	 * @throws InvalidRequestException when the file is empty, too large, of a
	 * disallowed type, or not a decodable image
	 */
	@Transactional
	public ImageResponse store(MultipartFile file, String altText, String caption, String actor) {
		if (file == null || file.isEmpty()) {
			throw new InvalidRequestException("Choose an image to upload.");
		}
		if (file.getSize() > properties.maxFileBytes()) {
			throw new InvalidRequestException("Images must be " + properties.maxFileMegabytes() + " MB or smaller.");
		}

		// The declared type is only a hint. It is checked against the allow list
		// and then confirmed against the actual bytes below.
		String mimeType = ImageValidator.resolveMimeType(file.getContentType());
		if (mimeType == null) {
			throw new InvalidRequestException(
					"That file type is not supported. Use a JPEG, PNG, GIF or WebP image.");
		}

		byte[] bytes;
		try {
			bytes = file.getBytes();
		}
		catch (IOException exception) {
			// The message never includes the exception, which could contain a path.
			throw new InvalidRequestException("The upload could not be read. Please try again.");
		}

		if (!ImageValidator.magicBytesMatch(bytes, mimeType)) {
			throw new InvalidRequestException("That file is not a valid image.");
		}

		int[] dimensions = ImageValidator.decode(bytes);
		if (dimensions == null) {
			// Failed to decode: almost certainly a script or HTML renamed to .png.
			throw new InvalidRequestException("That file could not be read as an image.");
		}

		String alt = altText == null ? "" : altText.trim();
		if (alt.isBlank()) {
			throw new InvalidRequestException("Alt text is required so the image is described for screen reader users.");
		}

		String filename = ImageValidator.generatedFileName(mimeType);
		Path target = resolveInsideStorage(filename);
		try {
			Files.createDirectories(storageRoot);
			Files.write(target, bytes, java.nio.file.StandardOpenOption.CREATE,
					java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE);
		}
		catch (IOException exception) {
			throw new InvalidRequestException("The image could not be saved. Please try again.");
		}

		String displayName = ImageValidator.sanitiseFileName(file.getOriginalFilename());
		ContentImage saved = this.imageRepository.save(new ContentImage(filename, displayName, mimeType,
				file.getSize(), dimensions[0], dimensions[1], alt, trimToNull(caption), actor, Instant.now()));

		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<ImageResponse> list() {
		return this.imageRepository.findAll().stream()
			.sorted(Comparator.comparing(ContentImage::getCreatedAt).reversed())
			.map(this::toResponse)
			.toList();
	}

	@Transactional
	public ImageResponse updateMetadata(Long id, ImageMetadataRequest request) {
		ContentImage image = require(id);
		image.setAltText(request.altText().trim());
		image.setCaption(trimToNull(request.caption()));
		return toResponse(this.imageRepository.save(image));
	}

	/** Removes the database row and the file. */
	@Transactional
	public void delete(String actor, Long id) {
		ContentImage image = require(id);
		try {
			Files.deleteIfExists(resolveInsideStorage(image.getFilename()));
		}
		catch (IOException exception) {
			// The row is removed regardless; a stranded file is preferable to a
			// row pointing at nothing.
		}
		this.imageRepository.delete(image);
	}

	/**
	 * Resolves a stored filename to a readable path.
	 *
	 * <p>The result is verified to sit inside the storage root, so even a
	 * tampered database value cannot read an arbitrary file.
	 */
	public Optional<Path> load(String filename) {
		if (filename == null || filename.isBlank()) {
			return Optional.empty();
		}
		// Only a bare file name is ever accepted; separators are rejected outright.
		if (filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
			return Optional.empty();
		}

		Path candidate = storageRoot.resolve(filename).normalize();
		if (!candidate.startsWith(storageRoot)) {
			return Optional.empty();
		}
		return Files.isRegularFile(candidate) ? Optional.of(candidate) : Optional.empty();
	}

	/**
	 * Builds the write path, asserting it stays inside the root.
	 *
	 * <p>Names are generated here rather than accepted, so this is belt and
	 * braces rather than the primary defence.
	 */
	private Path resolveInsideStorage(String filename) {
		Path candidate = storageRoot.resolve(filename).normalize();
		if (!candidate.startsWith(storageRoot) || candidate.equals(storageRoot)) {
			throw new InvalidRequestException("The image could not be saved.");
		}
		return candidate;
	}

	private ImageResponse toResponse(ContentImage image) {
		return new ImageResponse(image.getId(), properties.publicBasePath() + image.getFilename(),
				image.getOriginalName(), image.getMimeType(), image.getSizeBytes(), image.getWidth(),
				image.getHeight(), image.getAltText(), image.getCaption(), image.getUploadedBy(),
				image.getCreatedAt());
	}

	private ContentImage require(Long id) {
		return this.imageRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No image found with id " + id + "."));
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}