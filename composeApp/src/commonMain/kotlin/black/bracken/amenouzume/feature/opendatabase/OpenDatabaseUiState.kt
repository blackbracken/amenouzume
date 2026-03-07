package black.bracken.amenouzume.feature.opendatabase

sealed interface OpenDatabaseUiState {
    data object Idle : OpenDatabaseUiState

    data class Loaded(val databases: List<OpenDatabaseEntry>) : OpenDatabaseUiState
}

data class OpenDatabaseEntry(
  val name: String,
  val path: String,
  val size: String,
)
