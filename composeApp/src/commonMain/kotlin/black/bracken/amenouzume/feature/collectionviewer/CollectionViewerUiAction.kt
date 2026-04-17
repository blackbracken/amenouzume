package black.bracken.amenouzume.feature.collectionviewer

import black.bracken.amenouzume.kernel.model.Tag

data class CollectionViewerUiAction(
  val onClose: () -> Unit,
  val onConsumeError: () -> Unit,
  val onTagClick: (Tag) -> Unit,
) {
  companion object {
    val Noop = CollectionViewerUiAction(
      onClose = {},
      onConsumeError = {},
      onTagClick = {},
    )
  }
}
