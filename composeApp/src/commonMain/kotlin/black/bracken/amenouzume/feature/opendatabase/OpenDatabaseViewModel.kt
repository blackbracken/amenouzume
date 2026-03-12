package black.bracken.amenouzume.feature.opendatabase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.feature.opendatabase.util.toSizeText
import black.bracken.amenouzume.kernel.model.VaultHistory
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
  private var databases by mutableStateOf<List<OpenDatabaseEntry>>(emptyList())

  init {
    loadingScope.launchTracked {
      vaultRepository.loadVaultHistories()
        .onSuccess { histories -> databases = histories.map { it.toEntry() } }
        .onFailure { errorMessage = it.message }
    }
  }

  val uiState: StateFlow<OpenDatabaseUiState> = moleculeState {
    OpenDatabaseUiState.Loaded(
      databases = databases,
      isLoading = loadingScope.isLoading,
      errorMessage = errorMessage,
    )
  }

  fun createVault(path: String) {
    loadingScope.launchTracked {
      errorMessage = null
      vaultRepository.createVault(path)
        .onSuccess { reloadVaults() }
        .onFailure { errorMessage = it.message }
    }
  }

  fun openVault(filePath: String) {
    loadingScope.launchTracked {
      errorMessage = null
      vaultRepository.openVault(filePath)
        .onSuccess { reloadVaults() }
        .onFailure { errorMessage = it.message }
    }
  }

  private suspend fun reloadVaults() {
    vaultRepository.loadVaultHistories()
      .onSuccess { paths -> databases = paths.map { it.toEntry() } }
  }
}

private fun VaultHistory.toEntry() = OpenDatabaseEntry(name = name, path = path, size = sizeBytes.toSizeText())
