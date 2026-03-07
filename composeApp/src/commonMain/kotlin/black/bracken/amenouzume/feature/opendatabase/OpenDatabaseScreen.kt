package black.bracken.amenouzume.feature.opendatabase

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OpenDatabaseScreen(
    viewModel: OpenDatabaseViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    OpenDatabaseContent(state = state.value)
}

@Composable
private fun OpenDatabaseContent(
    state: OpenDatabaseUiState,
) {
}
