package black.bracken.amenouzume.feature.addcollection.composable

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.select_tags_create
import amenouzume.composeapp.generated.resources.select_tags_manage
import amenouzume.composeapp.generated.resources.select_tags_recommended
import amenouzume.composeapp.generated.resources.select_tags_search_placeholder
import amenouzume.composeapp.generated.resources.select_tags_title
import amenouzume.composeapp.generated.resources.select_tags_update
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.uishared.component.SelectItemsBottomSheet

@Composable
internal fun SelectTagsBottomSheet(
  selectedTags: List<Tag>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  searchResultTags: List<Tag>,
  recentTags: List<Tag>,
  onToggleTag: (Tag) -> Unit,
  onAttachTag: (Tag) -> Unit,
  onCreateTag: (String) -> Unit,
  onNavigateToManageTags: () -> Unit,
  onDismiss: () -> Unit,
  snackbarHostState: SnackbarHostState,
) {
  SelectItemsBottomSheet(
    selectedItems = selectedTags,
    searchQuery = searchQuery,
    onSearchQueryChange = onSearchQueryChange,
    searchResultItems = searchResultTags,
    recentItems = recentTags,
    onToggleItem = onToggleTag,
    onAttachItem = onAttachTag,
    onCreateItem = onCreateTag,
    onNavigateToManage = onNavigateToManageTags,
    onDismiss = onDismiss,
    snackbarHostState = snackbarHostState,
    itemName = { it.primaryName },
    itemKey = { it.id.value },
    icon = Icons.Default.Numbers,
    titleRes = Res.string.select_tags_title,
    searchPlaceholderRes = Res.string.select_tags_search_placeholder,
    createTextRes = Res.string.select_tags_create,
    manageTextRes = Res.string.select_tags_manage,
    updateTextRes = Res.string.select_tags_update,
    recommendedTextRes = Res.string.select_tags_recommended,
  )
}
