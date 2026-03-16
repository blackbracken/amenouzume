package black.bracken.amenouzume.feature.addcollection

import black.bracken.amenouzume.feature.collectionlist.CollectionCategory

data class AddCollectionUiAction(
  val onClose: () -> Unit,
  val onSaveDraft: () -> Unit,
  val onSelectCategory: (CollectionCategory) -> Unit,
  val onUploadArtwork: () -> Unit,
  val onUpdateTitle: (String) -> Unit,
  val onUpdateTags: (List<String>) -> Unit,
  val onAddTag: (String) -> Unit,
  val onTogglePublic: (Boolean) -> Unit,
  val onSubmit: () -> Unit,
  val onNavigateToCollections: () -> Unit,
) {
  companion object {
    val Noop = AddCollectionUiAction(
      onClose = {},
      onSaveDraft = {},
      onSelectCategory = {},
      onUploadArtwork = {},
      onUpdateTitle = {},
      onUpdateTags = {},
      onAddTag = {},
      onTogglePublic = {},
      onSubmit = {},
      onNavigateToCollections = {},
    )
  }
}
