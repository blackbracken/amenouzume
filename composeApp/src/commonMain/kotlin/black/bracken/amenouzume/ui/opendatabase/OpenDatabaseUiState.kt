package black.bracken.amenouzume.ui.opendatabase

sealed interface OpenDatabaseUiState {
    data object Idle : OpenDatabaseUiState
}
