package black.bracken.amenouzume.feature.manageauthor

import black.bracken.amenouzume.kernel.model.Author
import black.bracken.amenouzume.kernel.model.AuthorAlias
import black.bracken.amenouzume.kernel.model.AuthorId
import black.bracken.amenouzume.uishared.ScreenUiState
import black.bracken.amenouzume.util.Loadable
import org.jetbrains.compose.resources.StringResource

data class ManageAuthorUiState(
  override val isBusy: Boolean = false,
  val authors: Loadable<List<Author>> = Loadable.Loading,
  val searchQuery: String = "",
  val searchResultAuthors: List<Author> = emptyList(),
  val errorMessage: StringResource? = null,
  val editingAuthor: EditingAuthor? = null,
) : ScreenUiState {
  data class EditingAuthor(
    val authorId: AuthorId,
    val initialPrimaryName: String,
    val initialAliases: List<AuthorAlias>,
    val pendingPrimaryName: String,
    val pendingAliasNames: List<String>,
    val newAliasInput: String,
  )
}
