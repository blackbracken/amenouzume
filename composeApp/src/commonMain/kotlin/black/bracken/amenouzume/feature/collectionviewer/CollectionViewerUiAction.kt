package black.bracken.amenouzume.feature.collectionviewer

data class CollectionViewerUiAction(
  val onClose: () -> Unit,
  val onConsumeError: () -> Unit,
) {
  companion object {
    val Noop = CollectionViewerUiAction(
      onClose = {},
      onConsumeError = {},
    )
  }
}
