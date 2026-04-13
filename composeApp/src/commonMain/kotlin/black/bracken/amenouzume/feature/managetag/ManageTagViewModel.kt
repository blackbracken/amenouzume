package black.bracken.amenouzume.feature.managetag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.repository.TagRepository
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.TrackedScope
import black.bracken.amenouzume.util.getOrNull
import black.bracken.amenouzume.util.launchWithCatching
import black.bracken.amenouzume.util.moleculeState
import black.bracken.amenouzume.util.runWithCatching
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
  private var editingTag by mutableStateOf<ManageTagUiState.EditingTag?>(null)

  private var searchJob: Job? = null

  val uiState: StateFlow<ManageTagUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): ManageTagUiState {
    val tagsFlow = remember { tagRepository.getAllTags() }
    val allTagsLoadable by tagsFlow.collectAsState(Loadable.Loading)

    val tagById = remember(allTagsLoadable) {
      allTagsLoadable.getOrNull()?.associateBy { it.id }.orEmpty()
    }

    val resolvedSearchResults = remember(searchResults, tagById) {
      searchResults.mapNotNull { tagById[it.id] }
    }

    return ManageTagUiState(
      isBusy = busyScope.isRunning,
      tags = allTagsLoadable,
      searchQuery = searchQuery,
      searchResultTags = resolvedSearchResults,
      errorMessage = errorMessage,
      editingTag = editingTag,
    )
  }

  fun onConsumeError() {
    errorMessage = null
  }

  fun onUpdateSearchQuery(value: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    searchQuery = value

    searchJob?.cancel()
    searchJob = launch {
      searchResults = tagRepository.searchTags(value, limit = SEARCH_LIMIT).getOrThrow()
    }
  }

  fun onDeleteTag(tag: Tag) = launchWithCatching({ errorMessage = it.messageRes }) {
    tagRepository.deleteTag(tag).getOrThrow()
  }

  fun onCreateTag(name: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    val trimmedName = name.trim()
    if (trimmedName.isEmpty()) return@launchWithCatching

    tagRepository.createTag(trimmedName).getOrThrow()
    searchQuery = ""
  }

  fun onShowEditTagSheet(tag: Tag) = launchWithCatching({ errorMessage = it.messageRes }) {
    val aliases = tagRepository.getAliasesOnce(tag.id).getOrThrow()
    val pendingAliases = aliases.map { it.name }

    editingTag = ManageTagUiState.EditingTag(
      tagId = tag.id,
      initialPrimaryName = tag.primaryName,
      initialAliases = aliases,
      pendingPrimaryName = tag.primaryName,
      pendingAliasNames = pendingAliases,
      newAliasInput = "",
    )
  }

  fun onDismissEditTagSheet() = launchWithCatching({ errorMessage = it.messageRes }) {
    val current = editingTag ?: return@launchWithCatching
    editingTag = null

    if (current.pendingPrimaryName.trim() != current.initialPrimaryName) {
      tagRepository.updatePrimaryName(current.tagId, current.pendingPrimaryName.trim()).getOrThrow()
    }

    val initialNames = current.initialAliases.map { it.name }.toSet()
    val currentNames = current.pendingAliasNames.toSet()

    val addedAliasNames = currentNames - initialNames
    if (addedAliasNames.isNotEmpty()) {
      tagRepository.addAliases(current.tagId, addedAliasNames).getOrThrow()
    }

    val removedAliasIds = current.initialAliases
      .filter { it.name !in currentNames }
      .map { it.id }
      .toSet()
    if (removedAliasIds.isNotEmpty()) {
      tagRepository.removeAliases(current.tagId, removedAliasIds).getOrThrow()
    }
  }

  fun onUpdateEditingPrimaryName(value: String) {
    editingTag = editingTag?.copy(pendingPrimaryName = value)
  }

  fun onUpdateEditingNewAliasInput(value: String) {
    editingTag = editingTag?.copy(newAliasInput = value)
  }

  fun onAddAlias() {
    val current = editingTag ?: return
    val trimmedAlias = current.newAliasInput.trim()
    if (trimmedAlias.isBlank() || trimmedAlias in current.pendingAliasNames) return

    editingTag = current.copy(
      pendingAliasNames = current.pendingAliasNames + trimmedAlias,
      newAliasInput = "",
    )
  }

  fun onRemoveAlias(alias: String) {
    val current = editingTag ?: return
    editingTag = current.copy(pendingAliasNames = current.pendingAliasNames - alias)
  }

  fun onClose() = runWithCatching({ errorMessage = it.messageRes }) {
    navigator.back()
  }

  companion object {
    private const val SEARCH_LIMIT = 10
  }
}
