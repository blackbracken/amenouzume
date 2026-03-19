package black.bracken.amenouzume.feature.addcollection

import black.bracken.amenouzume.feature.collectionlist.CollectionCategory

data class AddCollectionUiAction(
  val onClose: () -> Unit,
  val onSelectCategory: (CollectionCategory) -> Unit,
  val onAddFiles: () -> Unit,
  val onUpdateTitle: (String) -> Unit,
  val onToggleTag: (String) -> Unit,
  val onAttachTag: (String) -> Unit,
  val onCreateTag: (String) -> Unit,
  val onSubmit: () -> Unit,
  val onNavigateToCollections: () -> Unit,
  val onNavigateToEditOrder: () -> Unit,
) {
  companion object {
    val Noop = AddCollectionUiAction(
      onClose = {},
      onSelectCategory = {},
      onAddFiles = {},
      onUpdateTitle = {},
      onToggleTag = {},
      onAttachTag = {},
      onCreateTag = {},
      onSubmit = {},
      onNavigateToCollections = {},
      onNavigateToEditOrder = {},
    )
  }
}
