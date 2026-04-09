package black.bracken.amenouzume.feature.addcollection.composable

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.select_authors_create
import amenouzume.composeapp.generated.resources.select_authors_manage
import amenouzume.composeapp.generated.resources.select_authors_recommended
import amenouzume.composeapp.generated.resources.select_authors_search_placeholder
import amenouzume.composeapp.generated.resources.select_authors_title
import amenouzume.composeapp.generated.resources.select_authors_update
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import black.bracken.amenouzume.kernel.model.Author
import black.bracken.amenouzume.uishared.component.SelectItemsBottomSheet

@Composable
internal fun SelectAuthorsBottomSheet(
  selectedAuthors: List<Author>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  searchResultAuthors: List<Author>,
  recentAuthors: List<Author>,
  onToggleAuthor: (Author) -> Unit,
  onAttachAuthor: (Author) -> Unit,
  onCreateAuthor: (String) -> Unit,
  onNavigateToManageAuthors: () -> Unit,
  onDismiss: () -> Unit,
  snackbarHostState: SnackbarHostState,
) {
  SelectItemsBottomSheet(
    selectedItems = selectedAuthors,
    searchQuery = searchQuery,
    onSearchQueryChange = onSearchQueryChange,
    searchResultItems = searchResultAuthors,
    recentItems = recentAuthors,
    onToggleItem = onToggleAuthor,
    onAttachItem = onAttachAuthor,
    onCreateItem = onCreateAuthor,
    onNavigateToManage = onNavigateToManageAuthors,
    onDismiss = onDismiss,
    snackbarHostState = snackbarHostState,
    itemName = { it.primaryName },
    itemKey = { it.id.value },
    icon = Icons.Default.Person,
    titleRes = Res.string.select_authors_title,
    searchPlaceholderRes = Res.string.select_authors_search_placeholder,
    createTextRes = Res.string.select_authors_create,
    manageTextRes = Res.string.select_authors_manage,
    updateTextRes = Res.string.select_authors_update,
    recommendedTextRes = Res.string.select_authors_recommended,
  )
}
