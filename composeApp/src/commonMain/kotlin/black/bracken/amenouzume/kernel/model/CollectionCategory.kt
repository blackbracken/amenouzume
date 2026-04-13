package black.bracken.amenouzume.kernel.model

enum class CollectionCategory(
  val acceptableMimeTypes: List<String>,
  val acceptableExtensions: Set<String>,
) {
  ILLUSTRATION(listOf("image/*"), IMAGE_EXTENSIONS),
  PHOTO(listOf("image/*"), IMAGE_EXTENSIONS),
  FANZINE(listOf("image/*", "application/pdf"), IMAGE_EXTENSIONS + "pdf"),
  MOVIE(listOf("video/*"), MOVIE_EXTENSIONS),
  ;

  fun acceptsFile(path: String): Boolean {
    val ext = path.substringAfterLast('.', "").lowercase()
    return ext in acceptableExtensions
  }
}

private val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg")
private val MOVIE_EXTENSIONS = setOf("mp4", "mkv", "avi", "mov", "webm")
