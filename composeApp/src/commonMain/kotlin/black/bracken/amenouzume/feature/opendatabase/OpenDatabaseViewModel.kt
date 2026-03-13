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
import black.bracken.amenouzume.util.TrackedScope
import black.bracken.amenouzume.util.launchWithCatching
import black.bracken.amenouzume.util.moleculeState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource

@Inject
@ViewModelKey(OpenDatabaseViewModel::class)
@ContributesIntoMap(AppScope::class)
class OpenDatabaseViewModel(
  private val vaultRepository: VaultRepository,
  private val navigator: Navigator,
) : ViewModel() {
  private val databasesTrackedScope = TrackedScope()
  private val actionTrackedScope = TrackedScope()
  private var errorMessage by mutableStateOf<StringResource?>(null)
  private var databases by mutableStateOf<List<OpenDatabaseEntry>?>(null)

  init {
    launchWithCatching({ errorMessage = it.messageRes }) {
      databasesTrackedScope.track {
        databases = vaultRepository.loadVaultHistories().map { it.toEntry() }
      }
    }
  }

  val uiState: StateFlow<OpenDatabaseUiState> = moleculeState {
    OpenDatabaseUiState(
      databases = databases,
      isBusy = databasesTrackedScope.isRunning || actionTrackedScope.isRunning,
      errorMessage = errorMessage,
    )
  }

  fun onCreateVault(path: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    errorMessage = null
    actionTrackedScope.track {
      vaultRepository.createVault(path)
      databases = vaultRepository.loadVaultHistories().map { it.toEntry() }
    }
  }

  fun onOpenVault(filePath: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    errorMessage = null
    actionTrackedScope.track {
      vaultRepository.openVault(filePath)
      databases = vaultRepository.loadVaultHistories().map { it.toEntry() }
    }
    navigator.navigate(CollectionListRoute(vaultPath = filePath))
  }
}

private fun VaultHistory.toEntry() = OpenDatabaseEntry(name = name, path = path, size = sizeBytes.toSizeText())
