package black.bracken.amenouzume.feature.managetag

import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagAlias
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.uishared.ScreenUiState
import black.bracken.amenouzume.util.Loadable
import org.jetbrains.compose.resources.StringResource

data class ManageTagUiState(
  override val isBusy: Boolean = false,
  val tags: Loadable<List<Tag>> = Loadable.Loading,
  val searchQuery: String = "",
  val searchResultTags: List<Tag> = emptyList(),
  val errorMessage: StringResource? = null,
  val editingTag: EditingTag? = null,
) : ScreenUiState {
  data class EditingTag(
    val tagId: TagId,
    val initialPrimaryName: String,
    val initialAliases: List<TagAlias>,
    val pendingPrimaryName: String,
    val pendingAliasNames: List<String>,
    val newAliasInput: String,
  )
}
