package black.bracken.amenouzume.feature.managetag

import black.bracken.amenouzume.kernel.model.Tag

data class ManageTagUiAction(
  val onClose: () -> Unit,
  val onUpdateSearchQuery: (String) -> Unit,
  val onDeleteTag: (Tag) -> Unit,
  val onCreateTag: (String) -> Unit,
  val onShowEditTagSheet: (Tag) -> Unit,
  val onDismissEditTagSheet: () -> Unit,
  val onUpdateEditingPrimaryName: (String) -> Unit,
  val onUpdateEditingNewAliasInput: (String) -> Unit,
  val onAddAlias: () -> Unit,
  val onRemoveAlias: (String) -> Unit,
) {
  companion object {
    val Noop = ManageTagUiAction(
      onClose = {},
      onUpdateSearchQuery = {},
      onDeleteTag = {},
      onCreateTag = {},
      onShowEditTagSheet = {},
      onDismissEditTagSheet = {},
      onUpdateEditingPrimaryName = {},
      onUpdateEditingNewAliasInput = {},
      onAddAlias = {},
      onRemoveAlias = {},
    )
  }
}
