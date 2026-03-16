package black.bracken.amenouzume.feature.addcollection

import black.bracken.amenouzume.feature.collectionlist.CollectionCategory

data class AddCollectionUiAction(
  val onClose: () -> Unit,
  val onSelectCategory: (CollectionCategory) -> Unit,
  val onAddFiles: () -> Unit,
  val onUpdateTitle: (String) -> Unit,
  val onUpdateTags: (List<String>) -> Unit,
  val onAddTag: (String) -> Unit,
  val onSubmit: () -> Unit,
  val onNavigateToCollections: () -> Unit,
) {
  companion object {
    val Noop = AddCollectionUiAction(
      onClose = {},
      onSelectCategory = {},
      onAddFiles = {},
      onUpdateTitle = {},
      onUpdateTags = {},
      onAddTag = {},
      onSubmit = {},
      onNavigateToCollections = {},
    )
  }
}
