package black.bracken.amenouzume.ui.opendatabase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

class OpenDatabaseViewModel : ViewModel() {
    private val events = MutableSharedFlow<OpenDatabaseEvent>(extraBufferCapacity = 20)

    val state: StateFlow<OpenDatabaseUiState> = viewModelScope.launchMolecule(RecompositionMode.Immediate) {
        openDatabasePresenter(events)
    }

    fun onEvent(event: OpenDatabaseEvent) {
        events.tryEmit(event)
    }
}
