package black.bracken.amenouzume.kernel.model

enum class CollectionFileType {
  IMAGE,
  PDF,
  VIDEO,
  ;

  companion object {
    private val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg")
    private val VIDEO_EXTENSIONS = setOf("mp4", "mkv", "avi", "mov", "webm")

    fun fromExtension(ext: String): CollectionFileType = when (ext.lowercase()) {
      "pdf" -> PDF
      in IMAGE_EXTENSIONS -> IMAGE
      in VIDEO_EXTENSIONS -> VIDEO
      else -> throw IllegalArgumentException("Unsupported file extension: $ext")
    }
  }
}
