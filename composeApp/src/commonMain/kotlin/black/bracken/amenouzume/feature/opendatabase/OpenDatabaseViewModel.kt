package black.bracken.amenouzume.feature.opendatabase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import black.bracken.amenouzume.platform.db.LibraryCreator
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OpenDatabaseViewModel(
  private val libraryCreator: LibraryCreator,
) : ViewModel() {
  private var isLoading by mutableStateOf(false)
  private var errorMessage by mutableStateOf<String?>(null)

  val state: StateFlow<OpenDatabaseUiState> =
    viewModelScope.launchMolecule(RecompositionMode.Immediate) {
      OpenDatabaseUiState.Loaded(
        databases = emptyList(),
        isLoading = isLoading,
        errorMessage = errorMessage,
      )
    }

  fun createLibrary(path: String) {
    viewModelScope.launch {
      isLoading = true
      errorMessage = null
      libraryCreator.create(path).onFailure { errorMessage = it.message }
      isLoading = false
    }
  }
}
