package black.bracken.amenouzume.feature.addcollection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.kernel.repository.CollectionRepository
import black.bracken.amenouzume.uishared.navigation.CollectionListRoute
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.TrackedScope
import black.bracken.amenouzume.util.launchWithCatching
import black.bracken.amenouzume.util.moleculeState
import black.bracken.amenouzume.util.runWithCatching
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource

@Inject
@ViewModelKey(AddCollectionViewModel::class)
@ContributesIntoMap(AppScope::class)
class AddCollectionViewModel(
  private val collectionRepository: CollectionRepository,
  private val navigator: Navigator,
) : ViewModel() {
  private val busyScope = TrackedScope()
  private var errorMessage by mutableStateOf<StringResource?>(null)
  private var title by mutableStateOf("")
  private var category by mutableStateOf("")

  val uiState: StateFlow<AddCollectionUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): AddCollectionUiState = AddCollectionUiState(
    title = title,
    category = category,
    isBusy = busyScope.isRunning,
    errorMessage = errorMessage,
  )

  fun onUpdateTitle(value: String) {
    title = value
  }

  fun onUpdateCategory(value: String) {
    category = value
  }

  fun onNavigateToCollections(vaultPath: String) = runWithCatching({ errorMessage = it.messageRes }) {
    navigator.navigateSingleTop(CollectionListRoute(vaultPath))
  }

  fun onAddCollection() = launchWithCatching({ errorMessage = it.messageRes }) {
    errorMessage = null
    busyScope.track {
      collectionRepository.addCollection(
        id = System.currentTimeMillis().toString(),
        title = title,
        category = category,
        contentType = category,
      )
    }
    navigator.back()
  }
}
