package black.bracken.amenouzume.feature.managetag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.repository.TagRepository
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
@ViewModelKey(ManageTagViewModel::class)
@ContributesIntoMap(AppScope::class)
class ManageTagViewModel(
  private val tagRepository: TagRepository,
  private val navigator: Navigator,
) : ViewModel() {
  private val busyScope = TrackedScope()
  private var errorMessage by mutableStateOf<StringResource?>(null)
  private var searchQuery by mutableStateOf("")
  private var searchResults by mutableStateOf<List<Tag>>(emptyList())

  val uiState: StateFlow<ManageTagUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): ManageTagUiState {
    val allTagsLoadable by tagRepository.allTags.collectAsState()

    return ManageTagUiState(
      isBusy = busyScope.isRunning,
      tags = allTagsLoadable,
      searchQuery = searchQuery,
      searchResultTags = searchResults,
      errorMessage = errorMessage,
    )
  }

  fun onUpdateSearchQuery(value: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    searchQuery = value
    searchResults = tagRepository.searchTags(value, limit = SEARCH_LIMIT)
  }

  fun onDeleteTag(tag: Tag) = launchWithCatching({ errorMessage = it.messageRes }) {
    tagRepository.deleteTag(tag)
  }

  fun onCreateTag(name: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return@launchWithCatching

    tagRepository.createTag(trimmed)
    searchQuery = ""
  }

  fun onClose() = runWithCatching({ errorMessage = it.messageRes }) {
    navigator.back()
  }

  companion object {
    private const val SEARCH_LIMIT = 10
  }
}
