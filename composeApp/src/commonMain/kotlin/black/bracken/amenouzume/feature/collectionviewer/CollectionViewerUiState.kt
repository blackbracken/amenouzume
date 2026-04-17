package black.bracken.amenouzume.feature.collectionviewer

import black.bracken.amenouzume.kernel.model.Author
import black.bracken.amenouzume.kernel.model.CollectionCategory
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.uishared.ScreenUiState
import black.bracken.amenouzume.util.Loadable
import org.jetbrains.compose.resources.StringResource

data class CollectionViewerUiState(
  val content: Loadable<Content> = Loadable.Loading,
  val errorMessage: StringResource? = null,
) : ScreenUiState {

  data class Content(
    val title: String,
    val category: CollectionCategory,
    val primaryFilePath: String?,
    val fileCount: Int,
    val tags: List<Tag>,
    val authors: List<Author>,
  )
}
