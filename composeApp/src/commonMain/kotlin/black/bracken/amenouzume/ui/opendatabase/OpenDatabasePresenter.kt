package black.bracken.amenouzume.ui.opendatabase

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

@Composable
fun openDatabasePresenter(events: Flow<OpenDatabaseEvent>): OpenDatabaseUiState {
    return OpenDatabaseUiState.Idle
}
