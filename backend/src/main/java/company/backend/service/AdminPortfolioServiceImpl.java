package company.backend.service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import company.backend.dto.PortfolioProjectResponse;
import company.backend.entity.PortfolioProject;
import company.backend.exception.InvalidRequestException;
import company.backend.exception.ResourceNotFoundException;
import company.backend.repo.PortfolioProjectRepository;

/**
 * Create, edit, publish, reorder and delete portfolio case studies.
 */
@Service
public class AdminPortfolioServiceImpl implements AdminPortfolioService {

	private static final int MAX_SLUG_LENGTH = 80;

	/** Matches the widest list column on the entity. */
	private static final int MAX_LIST_LENGTH = 1000;

	private final PortfolioProjectRepository portfolioProjectRepository;

	public AdminPortfolioServiceImpl(PortfolioProjectRepository portfolioProjectRepository) {
		this.portfolioProjectRepository = portfolioProjectRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PortfolioProjectResponse> findAll() {
		return this.portfolioProjectRepository.findAllByOrderByDisplayOrderAsc().stream()
			.map(PortfolioProjectResponse::from)
			.toList();
	}

	@Override
	@Transactional
	public PortfolioProjectResponse create(String actor, PortfolioProjectCommand command) {
		String slug = normaliseSlug(command.slug(), command.name());
		if (this.portfolioProjectRepository.findBySlug(slug).isPresent()) {
			throw new InvalidRequestException("A case study with the slug '" + slug + "' already exists.");
		}

		PortfolioProject project = new PortfolioProject(slug, require(command.name(), "Project name is required."),
				require(command.industry(), "Industry is required."),
				require(command.summary(), "A short description is required."),
				require(command.problem(), "A challenge description is required."),
				require(command.solution(), "A solution description is required."), join(command.technologies()),
				join(command.keyFeatures()), join(command.results()), trimToNull(command.imageUrl()),
				command.featured(), command.sampleProject(), resolveOrder(command.displayOrder()));

		project.setCategory(trimToNull(command.category()));
		project.setPublished(command.published());

		return PortfolioProjectResponse.from(this.portfolioProjectRepository.save(project));
	}

	@Override
	@Transactional
	public PortfolioProjectResponse update(String actor, Long id, PortfolioProjectCommand command) {
		PortfolioProject project = require(id);

		String slug = normaliseSlug(command.slug(), command.name());
		this.portfolioProjectRepository.findBySlug(slug)
			.filter(other -> !other.getId().equals(id))
			.ifPresent(other -> {
				throw new InvalidRequestException("A case study with the slug '" + slug + "' already exists.");
			});

		project.setSlug(slug);
		project.setName(require(command.name(), "Project name is required."));
		project.setIndustry(require(command.industry(), "Industry is required."));
		project.setSummary(require(command.summary(), "A short description is required."));
		project.setProblem(require(command.problem(), "A challenge description is required."));
		project.setSolution(require(command.solution(), "A solution description is required."));
		project.setTechnologies(join(command.technologies()));
		project.setKeyFeatures(join(command.keyFeatures()));
		project.setResults(join(command.results()));
		project.setImageUrl(trimToNull(command.imageUrl()));
		project.setCategory(trimToNull(command.category()));
		project.setFeatured(command.featured());
		project.setSampleProject(command.sampleProject());
		project.setPublished(command.published());
		if (command.displayOrder() != null) {
			project.setDisplayOrder(command.displayOrder());
		}
		project.touch(Instant.now());

		return PortfolioProjectResponse.from(this.portfolioProjectRepository.save(project));
	}

	@Override
	@Transactional
	public PortfolioProjectResponse setPublished(String actor, Long id, boolean published) {
		PortfolioProject project = require(id);
		project.setPublished(published);
		project.touch(Instant.now());
		return PortfolioProjectResponse.from(this.portfolioProjectRepository.save(project));
	}

	/**
	 * Applies a new ordering. Only ids that exist are moved, so a stale admin
	 * table cannot fail the whole save.
	 */
	@Override
	@Transactional
	public PortfolioProjectResponse reorder(String actor, List<Long> orderedIds) {
		if (orderedIds == null || orderedIds.isEmpty()) {
			throw new InvalidRequestException("Provide the case studies in their new order.");
		}

		Instant now = Instant.now();
		int order = 1;
		PortfolioProject last = null;
		for (Long id : orderedIds) {
			PortfolioProject project = this.portfolioProjectRepository.findById(id).orElse(null);
			if (project != null) {
				project.setDisplayOrder(order++);
				project.touch(now);
				this.portfolioProjectRepository.save(project);
				last = project;
			}
		}
		if (last == null) {
			throw new ResourceNotFoundException("None of the supplied case studies could be found.");
		}
		return PortfolioProjectResponse.from(last);
	}

	@Override
	@Transactional
	public void delete(String actor, Long id) {
		this.portfolioProjectRepository.delete(require(id));
	}

	private PortfolioProject require(Long id) {
		return this.portfolioProjectRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No portfolio project found with id " + id + "."));
	}

	/**
	 * Slugs are part of the public URL, so they are normalised to a safe
	 * character set, falling back to a slug derived from the name.
	 */
	private String normaliseSlug(String slug, String name) {
		String candidate = trimToNull(slug);
		if (candidate == null) {
			candidate = require(name, "Project name is required.");
		}
		String normalised = candidate.toLowerCase(Locale.ROOT)
			.replaceAll("[^a-z0-9]+", "-")
			.replaceAll("^-+|-+$", "");
		if (normalised.isEmpty()) {
			throw new InvalidRequestException("The slug must contain at least one letter or number.");
		}
		return normalised.substring(0, Math.min(normalised.length(), MAX_SLUG_LENGTH));
	}

	private int resolveOrder(Integer requested) {
		if (requested != null && requested >= 0) {
			return requested;
		}
		return (int) this.portfolioProjectRepository.count() + 1;
	}

	/** Stores a list as the comma separated text the entity already stores. */
	private String join(List<String> values) {
		if (values == null || values.isEmpty()) {
			return "";
		}
		String joined = values.stream()
			.map(this::trimToNull)
			.filter(value -> value != null)
			.collect(java.util.stream.Collectors.joining(","));
		// The entity columns are bounded, so an oversized list is rejected here
		// rather than failing later with a constraint violation.
		if (joined.length() > MAX_LIST_LENGTH) {
			throw new InvalidRequestException("List entries must add up to " + MAX_LIST_LENGTH + " characters or fewer.");
		}
		return joined;
	}

	private String require(String value, String message) {
		String trimmed = trimToNull(value);
		if (trimmed == null) {
			throw new InvalidRequestException(message);
		}
		return trimmed;
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}