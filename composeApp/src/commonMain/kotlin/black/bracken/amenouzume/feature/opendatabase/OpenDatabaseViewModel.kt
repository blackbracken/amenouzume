package black.bracken.amenouzume.feature.opendatabase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.kernel.repository.VaultRepository
import black.bracken.amenouzume.util.LoadingScope
import black.bracken.amenouzume.util.launchTracked
import black.bracken.amenouzume.util.moleculeState
import kotlinx.coroutines.flow.StateFlow

class OpenDatabaseViewModel(
  private val vaultRepository: VaultRepository,
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

  fun createVault(path: String) {
    loadingScope.launchTracked {
      errorMessage = null
      vaultRepository.createVault(path).onFailure { errorMessage = it.message }
    }
  }
}
