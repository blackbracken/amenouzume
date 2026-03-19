package black.bracken.amenouzume.feature.addcollection

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_unsupported_file_type
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.kernel.repository.CollectionRepository
import black.bracken.amenouzume.kernel.repository.TagRepository
import black.bracken.amenouzume.uishared.navigation.CollectionListRoute
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.Loadable
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
  private val tagRepository: TagRepository,
  private val navigator: Navigator,
) : ViewModel() {
  private val busyScope = TrackedScope()
  private var errorMessage by mutableStateOf<StringResource?>(null)
  private var selectedCategory by mutableStateOf<CollectionCategory?>(null)
  private var title by mutableStateOf("")
  private var filePaths by mutableStateOf<List<String>>(emptyList())
  private var tags by mutableStateOf<List<String>>(emptyList())

  val uiState: StateFlow<AddCollectionUiState> = moleculeState { presenter() }

  init {
    refreshAvailableTags()
  }

  private fun refreshAvailableTags() = launchWithCatching({ errorMessage = it.messageRes }) {
    tagRepository.refreshAllPrimaryNames()
  }

  @Composable
  private fun presenter(): AddCollectionUiState {
    val availableTagsLoadable by tagRepository.getAllPrimaryNames().collectAsState(Loadable.Loading)
    val availableTags = when (val l = availableTagsLoadable) {
      is Loadable.Loaded -> l.value
      else -> emptyList()
    }

    return AddCollectionUiState(
      isBusy = busyScope.isRunning,
      selectedCategory = selectedCategory,
      editing = if (selectedCategory != null) {
        AddCollectionUiState.Editing(
          title = title,
          filePaths = filePaths,
          authors = emptyList(),
          tags = tags,
          availableTags = availableTags,
        )
      } else {
        null
      },
      errorMessage = errorMessage,
    )
  }

  fun onSelectCategory(category: CollectionCategory) {
    selectedCategory = category
  }

  fun onAddFiles(paths: List<String>) = runWithCatching({ errorMessage = it.messageRes }) {
    val category = selectedCategory ?: return@runWithCatching
    val (valid, invalid) = paths.partition { category.acceptsFile(it) }

    if (invalid.isNotEmpty()) {
      throw CommonFailure(Res.string.error_unsupported_file_type)
    } else {
      filePaths = (filePaths + valid).distinct()
    }
  }

  fun onUpdateTitle(value: String) {
    title = value
  }

  fun onUpdateTags(value: List<String>) {
    tags = value
  }

  fun onAddTag(name: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return@launchWithCatching

    tagRepository.addTag(trimmed)

    if (trimmed !in tags) {
      tags = tags + trimmed
    }
  }

  fun onClose() = runWithCatching({ errorMessage = it.messageRes }) {
    navigator.back()
  }

  fun onNavigateToCollections(vaultPath: String) = runWithCatching({ errorMessage = it.messageRes }) {
    navigator.navigateSingleTop(CollectionListRoute(vaultPath))
  }

  fun onAddCollection() = launchWithCatching({ errorMessage = it.messageRes }) {
    errorMessage = null
    busyScope.track {
      collectionRepository.addCollection(
        title = title,
        category = selectedCategory?.name.orEmpty(),
        contentType = selectedCategory?.name.orEmpty(),
      )
    }
    navigator.back()
  }
}
