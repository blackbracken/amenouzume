package black.bracken.amenouzume.feature.opendatabase

data class OpenDatabaseUiAction(
  val onCreateDatabase: () -> Unit,
  val onBrowseFiles: () -> Unit,
  val onRetry: () -> Unit,
  val onOpenEntry: (OpenDatabaseEntry) -> Unit,
  val onDeleteEntry: (OpenDatabaseEntry) -> Unit,
) {
  companion object {
    val Noop = OpenDatabaseUiAction(
      onCreateDatabase = {},
      onBrowseFiles = {},
      onRetry = {},
      onOpenEntry = {},
      onDeleteEntry = {},
    )
  }
}
