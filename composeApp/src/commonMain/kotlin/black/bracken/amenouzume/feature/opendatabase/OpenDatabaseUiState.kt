package black.bracken.amenouzume.feature.opendatabase

sealed interface OpenDatabaseUiState {
    data object Idle : OpenDatabaseUiState
}
