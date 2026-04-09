package black.bracken.amenouzume.uishared.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectItemsBottomSheet(
  selectedItems: List<T>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  searchResultItems: List<T>,
  recentItems: List<T>,
  onToggleItem: (T) -> Unit,
  onAttachItem: (T) -> Unit,
  onCreateItem: (String) -> Unit,
  onNavigateToManage: () -> Unit,
  onDismiss: () -> Unit,
  snackbarHostState: SnackbarHostState,
  itemName: (T) -> String,
  itemKey: (T) -> Any,
  icon: ImageVector,
  titleRes: StringResource,
  searchPlaceholderRes: StringResource,
  createTextRes: StringResource,
  manageTextRes: StringResource,
  updateTextRes: StringResource,
  recommendedTextRes: StringResource,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
  ) {
    SelectItemsContent(
      selectedItems = selectedItems,
      searchQuery = searchQuery,
      onSearchQueryChange = onSearchQueryChange,
      searchResultItems = searchResultItems,
      recentItems = recentItems,
      onRemoveItem = onToggleItem,
      onCreateItem = onCreateItem,
      onAttachItem = onAttachItem,
      onNavigateToManage = onNavigateToManage,
      onDone = onDismiss,
      itemName = itemName,
      itemKey = itemKey,
      icon = icon,
      titleRes = titleRes,
      searchPlaceholderRes = searchPlaceholderRes,
      createTextRes = createTextRes,
      manageTextRes = manageTextRes,
      updateTextRes = updateTextRes,
      recommendedTextRes = recommendedTextRes,
    )
    SnackbarHost(snackbarHostState)
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ColumnScope.SelectItemsContent(
  selectedItems: List<T>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  searchResultItems: List<T>,
  recentItems: List<T>,
  onRemoveItem: (T) -> Unit,
  onCreateItem: (String) -> Unit,
  onAttachItem: (T) -> Unit,
  onNavigateToManage: () -> Unit,
  onDone: () -> Unit,
  itemName: (T) -> String,
  itemKey: (T) -> Any,
  icon: ImageVector,
  titleRes: StringResource,
  searchPlaceholderRes: StringResource,
  createTextRes: StringResource,
  manageTextRes: StringResource,
  updateTextRes: StringResource,
  recommendedTextRes: StringResource,
) {
  LazyColumn(modifier = Modifier.weight(1f)) {
    item(key = "header") {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = stringResource(titleRes),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChange,
          placeholder = { Text(stringResource(searchPlaceholderRes)) },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          leadingIcon = {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          },
          shape = MaterialTheme.shapes.small,
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
          ),
        )

        AnimatedVisibility(selectedItems.isNotEmpty()) {
          Column {
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy((-8).dp),
            ) {
              selectedItems.forEach { item ->
                InputChip(
                  selected = true,
                  onClick = { onRemoveItem(item) },
                  label = { Text(itemName(item), fontWeight = FontWeight.Medium) },
                  trailingIcon = {
                    Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = null,
                      modifier = Modifier.size(16.dp),
                    )
                  },
                  colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
                  ),
                )
              }
            }
          }
        }
      }
    }

    val showCreateItem = searchQuery.isNotBlank() &&
      searchResultItems.none { itemName(it).equals(searchQuery.trim(), ignoreCase = true) }
    val showSearchSuggestions = searchResultItems.isNotEmpty()
    val showSuggestionsAnything = showCreateItem || showSearchSuggestions

    if (showSuggestionsAnything) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
      }
    }

    if (showCreateItem) {
      val trimmedQuery = searchQuery.trim()

      item(key = "create_item") {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onCreateItem(trimmedQuery) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            text = stringResource(createTextRes, trimmedQuery),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
          )
        }
      }
    }

    if (showSearchSuggestions) {
      items(searchResultItems, key = { "search_${itemKey(it)}" }) { item ->
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onAttachItem(item) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            text = itemName(item),
            style = MaterialTheme.typography.bodyLarge,
          )
        }
      }
    }

    if (showSuggestionsAnything) {
      item(key = "search_results_divider") {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
      }
    }

    item(key = "section_header") {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(text = stringResource(recommendedTextRes))
        Spacer(modifier = Modifier.height(12.dp))
      }
    }

    items(recentItems, key = { itemKey(it) }) { item ->
      HorizontalDivider(modifier = Modifier.fillMaxWidth())
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onAttachItem(item) }
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
          text = itemName(item),
          style = MaterialTheme.typography.bodyLarge,
        )
      }
    }

    if (recentItems.isNotEmpty()) {
      item {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
      }
    }

    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToManage() }
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
          text = stringResource(manageTextRes),
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.weight(1f),
        )
        Icon(
          imageVector = Icons.Default.ChevronRight,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    item {
      HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
  }

  Button(
    onClick = onDone,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 16.dp),
    shape = MaterialTheme.shapes.medium,
    colors = ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
    ),
  ) {
    Text(
      text = stringResource(updateTextRes),
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(vertical = 8.dp),
    )
  }
}

@Composable
private fun SectionHeader(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.Bold,
  )
}
