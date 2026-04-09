package black.bracken.amenouzume.feature.manageauthor

import black.bracken.amenouzume.kernel.model.Author

data class ManageAuthorUiAction(
  val onClose: () -> Unit,
  val onUpdateSearchQuery: (String) -> Unit,
  val onDeleteAuthor: (Author) -> Unit,
  val onCreateAuthor: (String) -> Unit,
  val onShowEditAuthorSheet: (Author) -> Unit,
  val onDismissEditAuthorSheet: () -> Unit,
  val onUpdateEditingPrimaryName: (String) -> Unit,
  val onUpdateEditingNewAliasInput: (String) -> Unit,
  val onAddAlias: () -> Unit,
  val onRemoveAlias: (String) -> Unit,
  val onConsumeError: () -> Unit,
) {
  companion object {
    val Noop = ManageAuthorUiAction(
      onClose = {},
      onUpdateSearchQuery = {},
      onDeleteAuthor = {},
      onCreateAuthor = {},
      onShowEditAuthorSheet = {},
      onDismissEditAuthorSheet = {},
      onUpdateEditingPrimaryName = {},
      onUpdateEditingNewAliasInput = {},
      onAddAlias = {},
      onRemoveAlias = {},
      onConsumeError = {},
    )
  }
}
