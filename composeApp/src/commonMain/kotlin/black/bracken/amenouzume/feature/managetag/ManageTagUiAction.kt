package black.bracken.amenouzume.feature.managetag

import black.bracken.amenouzume.kernel.model.Tag

data class ManageTagUiAction(
  val onClose: () -> Unit,
  val onUpdateSearchQuery: (String) -> Unit,
  val onDeleteTag: (Tag) -> Unit,
  val onCreateTag: (String) -> Unit,
) {
  companion object {
    val Noop = ManageTagUiAction(
      onClose = {},
      onUpdateSearchQuery = {},
      onDeleteTag = {},
      onCreateTag = {},
    )
  }
}
