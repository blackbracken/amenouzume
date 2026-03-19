package black.bracken.amenouzume.feature.collectionlist

data class CollectionListUiAction(
  val onNavigateToAdd: () -> Unit,
) {
  companion object {
    val Noop = CollectionListUiAction(
      onNavigateToAdd = {},
    )
  }
}
