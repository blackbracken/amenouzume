package black.bracken.amenouzume.feature.opendatabase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.feature.opendatabase.util.toSizeText
import black.bracken.amenouzume.kernel.model.VaultHistory
import black.bracken.amenouzume.kernel.repository.VaultRepository
import black.bracken.amenouzume.uishared.navigation.CollectionListRoute
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.TrackedScope
import black.bracken.amenouzume.util.launchWithCatching
import black.bracken.amenouzume.util.map
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
  private val busyScope = TrackedScope()
  private var errorMessage by mutableStateOf<StringResource?>(null)

  init {
    launchWithCatching({ errorMessage = it.messageRes }) {
      vaultRepository.refreshVaultHistories()
    }
  }

  val uiState: StateFlow<OpenDatabaseUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): OpenDatabaseUiState {
    val databases by vaultRepository.getVaultHistories().collectAsState(Loadable.Loading)

    return OpenDatabaseUiState(
      isBusy = busyScope.isRunning,
      databases = databases.map { histories -> histories.map { it.toEntry() } },
      errorMessage = errorMessage,
    )
  }

  fun onDeleteEntry(entry: OpenDatabaseEntry) =
    launchWithCatching({ errorMessage = it.messageRes }) {
      vaultRepository.removeVaultHistory(entry.path)
    }

  fun onRetry() =
    launchWithCatching({ errorMessage = it.messageRes }) {
      vaultRepository.refreshVaultHistories()
    }

  fun onCreateVault(path: String) =
    launchWithCatching({ errorMessage = it.messageRes }) {
      errorMessage = null
      busyScope.track {
        vaultRepository.createVault(path)
      }
    }

  fun onOpenVault(filePath: String) =
    launchWithCatching({ errorMessage = it.messageRes }) {
      errorMessage = null
      busyScope.track {
        vaultRepository.openVault(filePath)
      }
      navigator.navigate(CollectionListRoute(vaultPath = filePath))
    }
}

private fun VaultHistory.toEntry() = OpenDatabaseEntry(name = name, path = path, size = sizeBytes.toSizeText())
