package black.bracken.amenouzume.feature.opendatabase

sealed interface OpenDatabaseUiState {
  data object Idle : OpenDatabaseUiState

  data class Loaded(
    val databases: List<OpenDatabaseEntry>,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
  ) : OpenDatabaseUiState
}

data class OpenDatabaseEntry(
  val name: String,
  val path: String,
  val size: String,
)
