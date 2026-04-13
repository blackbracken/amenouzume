package black.bracken.amenouzume.kernel.model

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.category_fanzine
import amenouzume.composeapp.generated.resources.category_illustration
import amenouzume.composeapp.generated.resources.category_movie
import amenouzume.composeapp.generated.resources.category_photo
import org.jetbrains.compose.resources.StringResource

enum class CollectionCategory(
  val labelRes: StringResource,
  val acceptableMimeTypes: List<String>,
  val acceptableExtensions: Set<String>,
) {
  ILLUSTRATION(Res.string.category_illustration, listOf("image/*"), IMAGE_EXTENSIONS),
  PHOTO(Res.string.category_photo, listOf("image/*"), IMAGE_EXTENSIONS),
  FANZINE(Res.string.category_fanzine, listOf("image/*", "application/pdf"), IMAGE_EXTENSIONS + "pdf"),
  MOVIE(Res.string.category_movie, listOf("video/*"), MOVIE_EXTENSIONS),
  ;

  fun acceptsFile(path: String): Boolean {
    val ext = path.substringAfterLast('.', "").lowercase()
    return ext in acceptableExtensions
  }
}

private val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg")
private val MOVIE_EXTENSIONS = setOf("mp4", "mkv", "avi", "mov", "webm")
