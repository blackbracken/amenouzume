package black.bracken.amenouzume.feature.opendatabase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.amenouzume.platform.db.LibraryCreator
import black.bracken.amenouzume.util.LoadingScope
import black.bracken.amenouzume.util.moleculeState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OpenDatabaseViewModel(
  private val libraryCreator: LibraryCreator,
) : ViewModel() {
  private val loadingScope = LoadingScope()
  private var errorMessage by mutableStateOf<String?>(null)

  val state: StateFlow<OpenDatabaseUiState> = moleculeState {
    OpenDatabaseUiState.Loaded(
      databases = emptyList(),
      isLoading = loadingScope.isLoading,
      errorMessage = errorMessage,
    )
  }

  fun createLibrary(path: String) {
    viewModelScope.launch {
      loadingScope.track {
        errorMessage = null
        libraryCreator.create(path).onFailure { errorMessage = it.message }
      }
    }
  }
}
