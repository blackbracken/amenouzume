package black.bracken.amenouzume.feature.addcollection

import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
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
  val onSubmit: () -> Unit,
  val onNavigateToCollections: () -> Unit,
  val onNavigateToEditOrder: () -> Unit,
  val onNavigateToManageTags: () -> Unit,
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
      onSubmit = {},
      onNavigateToCollections = {},
      onNavigateToEditOrder = {},
      onNavigateToManageTags = {},
    )
  }
}
