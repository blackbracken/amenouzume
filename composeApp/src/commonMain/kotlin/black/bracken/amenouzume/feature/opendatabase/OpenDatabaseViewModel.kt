package black.bracken.amenouzume.feature.opendatabase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.kernel.repository.VaultRepository
import black.bracken.amenouzume.uishared.navigation.CollectionListRoute
import black.bracken.amenouzume.uishared.navigation.Navigator
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

  val uiState: StateFlow<OpenDatabaseUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): OpenDatabaseUiState {
    val databases by vaultRepository.vaultHistories.collectAsState()

    return OpenDatabaseUiState(
      isBusy = busyScope.isRunning,
      databases = databases.map { histories -> histories.map { OpenDatabaseEntry.from(it) } },
      errorMessage = errorMessage,
    )
  }

  fun onConsumeError() {
    errorMessage = null
  }

  fun onOpenEntry(entry: OpenDatabaseEntry) = launchWithCatching({ errorMessage = it.messageRes }) {
    busyScope.track {
      vaultRepository.openVault(entry.path).getOrThrow()
    }
    navigator.navigateReplace(CollectionListRoute(vaultPath = entry.path, showAddFab = true))
  }

  fun onDeleteEntry(entry: OpenDatabaseEntry) = launchWithCatching({ errorMessage = it.messageRes }) {
    vaultRepository.removeVaultHistory(entry.path).getOrThrow()
  }

  fun onRetry() = launchWithCatching({ errorMessage = it.messageRes }) {
    vaultRepository.refreshVaultHistories()
  }

  fun onCreateVault(path: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    val vaultPath = busyScope.track {
      vaultRepository.createVault(path).getOrThrow().also { vaultRepository.openVault(it).getOrThrow() }
    }
    navigator.navigateReplace(CollectionListRoute(vaultPath = vaultPath, showAddFab = true))
  }

  fun onOpenVault(filePath: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    busyScope.track {
      vaultRepository.openVault(filePath).getOrThrow()
    }
    navigator.navigateReplace(CollectionListRoute(vaultPath = filePath, showAddFab = true))
  }
}
