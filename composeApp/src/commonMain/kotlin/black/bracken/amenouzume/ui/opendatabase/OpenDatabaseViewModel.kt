package black.bracken.amenouzume.ui.opendatabase

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.flow.StateFlow

class OpenDatabaseViewModel : ViewModel() {
    val state: StateFlow<OpenDatabaseUiState> = viewModelScope.launchMolecule(RecompositionMode.Immediate) {
        presenter()
    }

    @Composable
    private fun presenter(): OpenDatabaseUiState {
        return OpenDatabaseUiState.Idle
    }
}
