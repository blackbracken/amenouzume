package black.bracken.amenouzume.feature.addcollection

import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
import black.bracken.amenouzume.kernel.model.Author
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.uishared.ScreenUiState
import org.jetbrains.compose.resources.StringResource

data class AddCollectionUiState(
  override val isBusy: Boolean,
  val selectedCategory: CollectionCategory?,
  val editing: Editing?,
  val errorMessage: StringResource?,
  val showTagsSheet: Boolean,
  val showAuthorsSheet: Boolean,
) : ScreenUiState {
  data class Editing(
    val title: String,
    val filePaths: List<String>,
    val authors: List<Author>,
    val authorSearchQuery: String,
    val availableAuthors: List<Author>,
    val searchResultAuthors: List<Author>,
    val recentAuthors: List<Author>,
    val tags: List<Tag>,
    val tagSearchQuery: String,
    val availableTags: List<Tag>,
    val searchResultTags: List<Tag>,
    val recentTags: List<Tag>,
  )
}
