package black.bracken.amenouzume.feature.addcollection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.kernel.repository.CollectionRepository
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.LoadingScope
import black.bracken.amenouzume.util.launchWithCatching
import black.bracken.amenouzume.util.moleculeState
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
  private val loadingScope = LoadingScope()
  private var errorMessage by mutableStateOf<StringResource?>(null)
  private var title by mutableStateOf("")
  private var category by mutableStateOf("")

  val uiState: StateFlow<AddCollectionUiState> = moleculeState {
    AddCollectionUiState(
      title = title,
      category = category,
      isLoading = loadingScope.isLoading,
      errorMessage = errorMessage,
    )
  }

  fun updateTitle(value: String) {
    title = value
  }

  fun updateCategory(value: String) {
    category = value
  }

  fun addCollection() = launchWithCatching({ errorMessage = it.messageRes }) {
    errorMessage = null
    loadingScope.track {
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
