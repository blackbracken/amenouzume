package black.bracken.amenouzume.feature.addcollection

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_unsupported_file_type
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.kernel.repository.CollectionRepository
import black.bracken.amenouzume.kernel.repository.TagRepository
import black.bracken.amenouzume.uishared.navigation.CollectionListRoute
import black.bracken.amenouzume.uishared.navigation.ManageTagRoute
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.TrackedScope
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.getOrNull
import black.bracken.amenouzume.util.launchWithCatching
import black.bracken.amenouzume.util.moleculeState
import black.bracken.amenouzume.util.runWithCatching
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import black.bracken.amenouzume.util.TimeProvider
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
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
  private var title by mutableStateOf(defaultTitle())
  private var filePaths by mutableStateOf<List<String>>(emptyList())
  private var selectedTagIds by mutableStateOf<Set<TagId>>(emptySet())
  private var tagSearchQuery by mutableStateOf("")
  private var searchResultTagIds by mutableStateOf<List<TagId>>(emptyList())
  private var showTagsSheet by mutableStateOf(false)

  val uiState: StateFlow<AddCollectionUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): AddCollectionUiState {
    val availableTagsLoadable by tagRepository.getAllTags().collectAsState(Loadable.Loading)
    val availableTags = availableTagsLoadable.getOrNull().orEmpty()

    val recentTags = remember(availableTags) { availableTags.take(3) }

    val tagById = remember(availableTags) { availableTags.associateBy { it.id } }
    val resolvedTags = remember(selectedTagIds, tagById) {
      selectedTagIds.mapNotNull { tagById[it] }.sorted()
    }

    val resolvedSearchResults = remember(searchResultTagIds, tagById) {
      searchResultTagIds.mapNotNull { tagById[it] }
    }

    val searchResultTags = remember(resolvedSearchResults, selectedTagIds) {
      resolvedSearchResults.filter { it.id !in selectedTagIds }
    }

    return AddCollectionUiState(
      isBusy = busyScope.isRunning,
      selectedCategory = selectedCategory,
      editing = if (selectedCategory != null) {
        AddCollectionUiState.Editing(
          title = title,
          filePaths = filePaths,
          authors = emptyList(),
          tags = resolvedTags,
          tagSearchQuery = tagSearchQuery,
          availableTags = availableTags,
          searchResultTags = searchResultTags,
          recentTags = recentTags,
        )
      } else {
        null
      },
      errorMessage = errorMessage,
      showTagsSheet = showTagsSheet,
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

  fun onUpdateTagSearchQuery(value: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    tagSearchQuery = value
    searchResultTagIds = tagRepository.searchTags(value, limit = SEARCH_LIMIT).map { it.id }
  }

  fun onToggleTag(tag: Tag) {
    selectedTagIds = if (tag.id in selectedTagIds) {
      selectedTagIds - tag.id
    } else {
      selectedTagIds + tag.id
    }
  }

  fun onAttachTag(tag: Tag) {
    selectedTagIds = selectedTagIds + tag.id
  }

  fun onCreateTag(name: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    val trimmedName = name.trim()
    if (trimmedName.isEmpty()) return@launchWithCatching

    val tag = tagRepository.createTag(trimmedName)
    selectedTagIds = selectedTagIds + tag.id
    tagSearchQuery = ""
  }

  fun onShowTagsSheet() {
    showTagsSheet = true
  }

  fun onDismissTagsSheet() {
    showTagsSheet = false
  }

  fun onClose() = runWithCatching({ errorMessage = it.messageRes }) {
    navigator.back()
  }

  fun onNavigateToManageTags() = runWithCatching({ errorMessage = it.messageRes }) {
    navigator.navigate(ManageTagRoute)
  }

  fun onNavigateToCollections(vaultPath: String) = runWithCatching({ errorMessage = it.messageRes }) {
    navigator.navigateSingleTop(CollectionListRoute(vaultPath))
  }

  fun onCreateCollection() = launchWithCatching({ errorMessage = it.messageRes }) {
    errorMessage = null
    busyScope.track {
      collectionRepository.createCollection(
        title = title,
        category = selectedCategory?.name.orEmpty(),
        contentType = selectedCategory?.name.orEmpty(),
      )
    }
    navigator.back()
  }

  companion object {
    private const val SEARCH_LIMIT = 5

    private fun defaultTitle(): String = TimeProvider.now()
      .toLocalDateTime(TimeZone.currentSystemDefault())
      .format(
        LocalDateTime.Format {
          year()
          monthNumber()
          day()
          hour()
          minute()
          second()
        },
      )
  }
}
