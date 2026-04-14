package black.bracken.amenouzume.feature.collectionlist

import black.bracken.amenouzume.kernel.model.CollectionId

data class CollectionListUiAction(
  val onNavigateToAdd: () -> Unit,
  val onOpenCollection: (CollectionId) -> Unit,
) {
  companion object {
    val Noop = CollectionListUiAction(
      onNavigateToAdd = {},
      onOpenCollection = {},
    )
  }
}
