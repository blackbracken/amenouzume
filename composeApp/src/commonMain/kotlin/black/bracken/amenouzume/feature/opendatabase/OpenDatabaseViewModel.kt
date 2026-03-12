package black.bracken.amenouzume.feature.opendatabase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.feature.opendatabase.util.toSizeText
import black.bracken.amenouzume.kernel.model.VaultHistory
import black.bracken.amenouzume.kernel.repository.VaultRepository
import black.bracken.amenouzume.uishared.navigation.CollectionListRoute
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.LoadingScope
import black.bracken.amenouzume.util.handleFailureWithMessage
import black.bracken.amenouzume.util.launchTracked
import black.bracken.amenouzume.util.moleculeState
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource

class OpenDatabaseViewModel(
  private val vaultRepository: VaultRepository,
  private val navigator: Navigator,
) : ViewModel() {
  private val loadingScope = LoadingScope()
  private var errorMessage by mutableStateOf<StringResource?>(null)
  private var databases by mutableStateOf<List<OpenDatabaseEntry>>(emptyList())

  init {
    loadingScope.launchTracked {
      vaultRepository.loadVaultHistories()
        .onSuccess { histories -> databases = histories.map { it.toEntry() } }
        .handleFailureWithMessage { errorMessage = it }
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
        .handleFailureWithMessage { errorMessage = it }
    }
  }

  fun openVault(filePath: String) {
    loadingScope.launchTracked {
      errorMessage = null
      vaultRepository.openVault(filePath)
        .onSuccess {
          reloadVaults()
          navigator.navigate(CollectionListRoute(vaultPath = filePath))
        }
        .handleFailureWithMessage { errorMessage = it }
    }
  }

  private suspend fun reloadVaults() {
    vaultRepository.loadVaultHistories()
      .onSuccess { paths -> databases = paths.map { it.toEntry() } }
      .handleFailureWithMessage { errorMessage = it }
  }

}

private fun VaultHistory.toEntry() = OpenDatabaseEntry(name = name, path = path, size = sizeBytes.toSizeText())
