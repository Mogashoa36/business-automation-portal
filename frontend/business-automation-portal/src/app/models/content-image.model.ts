/** An image held in the admin media library. */
export interface ContentImage {
  readonly id: number;
  /** Server generated URL. The only path the browser ever uses. */
  readonly url: string;
  /** The uploader's original filename, shown for reference only. */
  readonly originalName: string;
  readonly mimeType: string;
  readonly sizeBytes: number;
  readonly width: number;
  readonly height: number;
  readonly altText: string;
  readonly caption: string | null;
  readonly uploadedBy: string;
  readonly createdAt: string;
}

/** Metadata edited after an image has been uploaded. */
export interface ImageMetadataPayload {
  readonly altText: string;
  readonly caption: string;
}