package black.bracken.amenouzume.ui.opendatabase

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OpenDatabaseScreen(
    viewModel: OpenDatabaseViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    OpenDatabaseContent(
        state = state.value,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun OpenDatabaseContent(
    state: OpenDatabaseUiState,
    onEvent: (OpenDatabaseEvent) -> Unit,
) {
}
