package black.bracken.amenouzume.feature.addcollection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
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
  private var selectedCategory by mutableStateOf<CollectionCategory?>(null)
  private var title by mutableStateOf("")
  private var tags by mutableStateOf<List<String>>(emptyList())
  private var availableTags by mutableStateOf(
    listOf("Architecture", "Design", "Engineering", "Marketing", "Photography", "UI/UX"),
  )
  private var isPublic by mutableStateOf(true)

  val uiState: StateFlow<AddCollectionUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): AddCollectionUiState =
    AddCollectionUiState(
      isBusy = busyScope.isRunning,
      selectedCategory = selectedCategory,
      editing = when (selectedCategory) {
        CollectionCategory.ILLUSTRATION -> AddCollectionUiState.Editing.Illustration(
          title = title,
          authors = emptyList(),
          tags = tags,
          availableTags = availableTags,
          isPublic = isPublic,
        )
        else -> null
      },
      errorMessage = errorMessage,
    )

  fun onSelectCategory(category: CollectionCategory) {
    selectedCategory = category
  }

  fun onUpdateTitle(value: String) {
    title = value
  }

  fun onUpdateTags(value: List<String>) {
    tags = value
  }

  fun onAddTag(name: String) {
    val trimmed = name.trim()
    if (trimmed.isNotEmpty() && trimmed !in availableTags) {
      availableTags = availableTags + trimmed
    }
    if (trimmed.isNotEmpty() && trimmed !in tags) {
      tags = tags + trimmed
    }
  }

  fun onTogglePublic(value: Boolean) {
    isPublic = value
  }

  fun onClose() =
    runWithCatching({ errorMessage = it.messageRes }) {
      navigator.back()
    }

  fun onNavigateToCollections(vaultPath: String) =
    runWithCatching({ errorMessage = it.messageRes }) {
      navigator.navigateSingleTop(CollectionListRoute(vaultPath))
    }

  fun onAddCollection() =
    launchWithCatching({ errorMessage = it.messageRes }) {
      errorMessage = null
      busyScope.track {
        collectionRepository.addCollection(
          id = System.currentTimeMillis().toString(),
          title = title,
          category = selectedCategory?.name.orEmpty(),
          contentType = selectedCategory?.name.orEmpty(),
        )
      }
      navigator.back()
    }
}
