package black.bracken.amenouzume.feature.addcollection

import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
import black.bracken.amenouzume.kernel.model.Author
import black.bracken.amenouzume.kernel.model.Tag

data class AddCollectionUiAction(
  val onClose: () -> Unit,
  val onSelectCategory: (CollectionCategory) -> Unit,
  val onAddFiles: () -> Unit,
  val onUpdateTitle: (String) -> Unit,
  val onUpdateTagSearchQuery: (String) -> Unit,
  val onToggleTag: (Tag) -> Unit,
  val onAttachTag: (Tag) -> Unit,
  val onCreateTag: (String) -> Unit,
  val onShowTagsSheet: () -> Unit,
  val onDismissTagsSheet: () -> Unit,
  val onUpdateAuthorSearchQuery: (String) -> Unit,
  val onToggleAuthor: (Author) -> Unit,
  val onAttachAuthor: (Author) -> Unit,
  val onCreateAuthor: (String) -> Unit,
  val onShowAuthorsSheet: () -> Unit,
  val onDismissAuthorsSheet: () -> Unit,
  val onSubmit: () -> Unit,
  val onNavigateToCollections: () -> Unit,
  val onNavigateToEditOrder: () -> Unit,
  val onNavigateToManageTags: () -> Unit,
  val onNavigateToManageAuthors: () -> Unit,
  val onConsumeError: () -> Unit,
) {
  companion object {
    val Noop = AddCollectionUiAction(
      onClose = {},
      onSelectCategory = {},
      onAddFiles = {},
      onUpdateTitle = {},
      onUpdateTagSearchQuery = {},
      onToggleTag = {},
      onAttachTag = {},
      onCreateTag = {},
      onShowTagsSheet = {},
      onDismissTagsSheet = {},
      onUpdateAuthorSearchQuery = {},
      onToggleAuthor = {},
      onAttachAuthor = {},
      onCreateAuthor = {},
      onShowAuthorsSheet = {},
      onDismissAuthorsSheet = {},
      onSubmit = {},
      onNavigateToCollections = {},
      onNavigateToEditOrder = {},
      onNavigateToManageTags = {},
      onNavigateToManageAuthors = {},
      onConsumeError = {},
    )
  }
}
