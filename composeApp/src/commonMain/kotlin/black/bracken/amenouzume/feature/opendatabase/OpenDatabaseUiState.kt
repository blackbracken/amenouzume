package black.bracken.amenouzume.feature.opendatabase

data class DatabaseEntry(
    val name: String,
    val path: String,
    val size: String,
)

sealed interface OpenDatabaseUiState {
    data object Idle : OpenDatabaseUiState

    data class Loaded(val databases: List<DatabaseEntry>) : OpenDatabaseUiState
}
