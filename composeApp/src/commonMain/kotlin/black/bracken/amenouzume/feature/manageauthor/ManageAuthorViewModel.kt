package black.bracken.amenouzume.feature.manageauthor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.kernel.model.Author
import black.bracken.amenouzume.kernel.repository.AuthorRepository
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
@ViewModelKey(ManageAuthorViewModel::class)
@ContributesIntoMap(AppScope::class)
class ManageAuthorViewModel(
  private val authorRepository: AuthorRepository,
  private val navigator: Navigator,
) : ViewModel() {
  private val busyScope = TrackedScope()
  private var errorMessage by mutableStateOf<StringResource?>(null)
  private var searchQuery by mutableStateOf("")
  private var searchResults by mutableStateOf<List<Author>>(emptyList())
  private var editingAuthor by mutableStateOf<ManageAuthorUiState.EditingAuthor?>(null)
  private var searchJob: Job? = null

  val uiState: StateFlow<ManageAuthorUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): ManageAuthorUiState {
    val authorsFlow = remember { authorRepository.getAllAuthors() }
    val allAuthorsLoadable by authorsFlow.collectAsState(Loadable.Loading)

    val authorById = remember(allAuthorsLoadable) {
      allAuthorsLoadable.getOrNull()?.associateBy { it.id }.orEmpty()
    }

    val resolvedSearchResults = remember(searchResults, authorById) {
      searchResults.mapNotNull { authorById[it.id] }
    }

    return ManageAuthorUiState(
      isBusy = busyScope.isRunning,
      authors = allAuthorsLoadable,
      searchQuery = searchQuery,
      searchResultAuthors = resolvedSearchResults,
      errorMessage = errorMessage,
      editingAuthor = editingAuthor,
    )
  }

  fun onConsumeError() {
    errorMessage = null
  }

  fun onUpdateSearchQuery(value: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    searchQuery = value

    searchJob?.cancel()
    searchJob = launch {
      searchResults = authorRepository.searchAuthors(value, limit = SEARCH_LIMIT).getOrThrow()
    }
  }

  fun onDeleteAuthor(author: Author) = launchWithCatching({ errorMessage = it.messageRes }) {
    authorRepository.deleteAuthor(author).getOrThrow()
  }

  fun onCreateAuthor(name: String) = launchWithCatching({ errorMessage = it.messageRes }) {
    val trimmedName = name.trim()
    if (trimmedName.isEmpty()) return@launchWithCatching

    authorRepository.createAuthor(trimmedName).getOrThrow()
    searchQuery = ""
  }

  fun onShowEditAuthorSheet(author: Author) = launchWithCatching({ errorMessage = it.messageRes }) {
    val aliases = authorRepository.getAliasesOnce(author.id).getOrThrow()
    val pendingAliases = aliases.map { it.name }

    editingAuthor = ManageAuthorUiState.EditingAuthor(
      authorId = author.id,
      initialPrimaryName = author.primaryName,
      initialAliases = aliases,
      pendingPrimaryName = author.primaryName,
      pendingAliasNames = pendingAliases,
      newAliasInput = "",
    )
  }

  fun onDismissEditAuthorSheet() = launchWithCatching({ errorMessage = it.messageRes }) {
    val current = editingAuthor ?: return@launchWithCatching

    val trimmedName = current.pendingPrimaryName.trim()
    if (trimmedName.isNotEmpty() && trimmedName != current.initialPrimaryName) {
      authorRepository.updatePrimaryName(current.authorId, trimmedName).getOrThrow()
    }

    val initialNames = current.initialAliases.map { it.name }.toSet()
    val currentNames = current.pendingAliasNames.toSet()

    val addedAliasNames = currentNames - initialNames
    if (addedAliasNames.isNotEmpty()) {
      authorRepository.addAliases(current.authorId, addedAliasNames).getOrThrow()
    }

    val removedAliasIds = current.initialAliases
      .filter { it.name !in currentNames }
      .map { it.id }
      .toSet()
    if (removedAliasIds.isNotEmpty()) {
      authorRepository.removeAliases(current.authorId, removedAliasIds).getOrThrow()
    }

    editingAuthor = null
  }

  fun onUpdateEditingPrimaryName(value: String) {
    editingAuthor = editingAuthor?.copy(pendingPrimaryName = value)
  }

  fun onUpdateEditingNewAliasInput(value: String) {
    editingAuthor = editingAuthor?.copy(newAliasInput = value)
  }

  fun onAddAlias() {
    val current = editingAuthor ?: return
    val trimmedAlias = current.newAliasInput.trim()
    if (trimmedAlias.isBlank() || trimmedAlias in current.pendingAliasNames) return

    editingAuthor = current.copy(
      pendingAliasNames = current.pendingAliasNames + trimmedAlias,
      newAliasInput = "",
    )
  }

  fun onRemoveAlias(alias: String) {
    val current = editingAuthor ?: return
    editingAuthor = current.copy(pendingAliasNames = current.pendingAliasNames - alias)
  }

  fun onClose() = runWithCatching({ errorMessage = it.messageRes }) {
    navigator.back()
  }

  companion object {
    private const val SEARCH_LIMIT = 10
  }
}
