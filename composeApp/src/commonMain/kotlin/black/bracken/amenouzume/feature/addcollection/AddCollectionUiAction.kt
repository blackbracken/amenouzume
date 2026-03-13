package black.bracken.amenouzume.feature.addcollection

data class AddCollectionUiAction(
  val onUpdateTitle: (String) -> Unit,
  val onUpdateCategory: (String) -> Unit,
  val onSubmit: () -> Unit,
  val onNavigateToCollections: () -> Unit,
) {
  companion object {
    val Noop = AddCollectionUiAction(
      onUpdateTitle = {},
      onUpdateCategory = {},
      onSubmit = {},
      onNavigateToCollections = {},
    )
  }
}
