package black.bracken.amenouzume.feature.collectionlist

data class CollectionListUiAction(
  val onBack: () -> Unit,
  val onNavigateToAdd: () -> Unit,
) {
  companion object {
    val Noop = CollectionListUiAction(
      onBack = {},
      onNavigateToAdd = {},
    )
  }
}
