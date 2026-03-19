package black.bracken.amenouzume.feature.addcollection

import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
import black.bracken.amenouzume.uishared.ScreenUiState
import org.jetbrains.compose.resources.StringResource

data class AddCollectionUiState(
  override val isBusy: Boolean,
  val selectedCategory: CollectionCategory?,
  val editing: Editing?,
  val errorMessage: StringResource?,
) : ScreenUiState {
  data class Editing(
    val title: String,
    val filePaths: List<String>,
    val authors: List<String>,
    val tags: List<String>,
    val availableTags: List<String>,
    val recentTags: List<String>,
  )
}
