package black.bracken.amenouzume.feature.addcollection.composable

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.select_tags_create
import amenouzume.composeapp.generated.resources.select_tags_manage
import amenouzume.composeapp.generated.resources.select_tags_recommended
import amenouzume.composeapp.generated.resources.select_tags_search_placeholder
import amenouzume.composeapp.generated.resources.select_tags_title
import amenouzume.composeapp.generated.resources.select_tags_update
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
import androidx.compose.material.icons.filled.Numbers
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
import kotlin.time.Instant
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
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
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
  ) {
    SelectTagsContent(
      selectedTags = selectedTags,
      searchQuery = searchQuery,
      onSearchQueryChange = onSearchQueryChange,
      searchResultTags = searchResultTags,
      recentTags = recentTags,
      onRemoveTag = onToggleTag,
      onCreateTag = onCreateTag,
      onAttachTag = onAttachTag,
      onNavigateToManageTags = onNavigateToManageTags,
      onDone = onDismiss,
    )
    SnackbarHost(snackbarHostState)
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ColumnScope.SelectTagsContent(
  selectedTags: List<Tag>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  searchResultTags: List<Tag>,
  recentTags: List<Tag>,
  onRemoveTag: (Tag) -> Unit,
  onCreateTag: (String) -> Unit,
  onAttachTag: (Tag) -> Unit,
  onNavigateToManageTags: () -> Unit,
  onDone: () -> Unit,
) {
  LazyColumn(modifier = Modifier.weight(1f)) {
    item(key = "header") {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = stringResource(Res.string.select_tags_title),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChange,
          placeholder = { Text(stringResource(Res.string.select_tags_search_placeholder)) },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Numbers,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          },
          shape = MaterialTheme.shapes.small,
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
          ),
        )

        AnimatedVisibility(selectedTags.isNotEmpty()) {
          Column {
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy((-8).dp),
            ) {
              selectedTags.forEach { tag ->
                InputChip(
                  selected = true,
                  onClick = { onRemoveTag(tag) },
                  label = { Text(tag.primaryName, fontWeight = FontWeight.Medium) },
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

    val showCreateTag = searchQuery.isNotBlank() &&
      searchResultTags.none { it.primaryName.equals(searchQuery.trim(), ignoreCase = true) }
    val showSearchSuggestions = searchResultTags.isNotEmpty()
    val showSuggestionsAnything = showCreateTag || showSearchSuggestions

    if (showSuggestionsAnything) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
      }
    }

    if (showCreateTag) {
      val trimmedQuery = searchQuery.trim()

      item(key = "create_tag") {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onCreateTag(trimmedQuery) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Default.Numbers,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            text = stringResource(Res.string.select_tags_create, trimmedQuery),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
          )
        }
      }
    }

    if (showSearchSuggestions) {
      items(searchResultTags, key = { "search_${it.id.value}" }) { tag ->
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onAttachTag(tag) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Default.Numbers,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            text = tag.primaryName,
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
        SectionHeader(text = stringResource(Res.string.select_tags_recommended))
        Spacer(modifier = Modifier.height(12.dp))
      }
    }

    items(recentTags, key = { it.id.value }) { tag ->
      HorizontalDivider(modifier = Modifier.fillMaxWidth())
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onAttachTag(tag) }
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Default.Numbers,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
          text = tag.primaryName,
          style = MaterialTheme.typography.bodyLarge,
        )
      }
    }

    if (recentTags.isNotEmpty()) {
      item {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
      }
    }

    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToManageTags() }
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
          text = stringResource(Res.string.select_tags_manage),
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
      text = stringResource(Res.string.select_tags_update),
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(vertical = 8.dp),
    )
  }
}

@Preview
@Composable
private fun SelectTagsContentSearchingPreview() {
  AmenouzumeTheme {
    Surface {
      Column {
        SelectTagsContent(
          selectedTags = (0L until 10L).map { Tag(TagId(it), "Tag-$it", Instant.DISTANT_PAST) },
          searchQuery = "Vaporw",
          onSearchQueryChange = {},
          searchResultTags = listOf(Tag(TagId(100), "Vaporwave", Instant.DISTANT_PAST), Tag(TagId(101), "Vaporwave Art", Instant.DISTANT_PAST)),
          recentTags = listOf(Tag(TagId(3), "Photography", Instant.DISTANT_PAST), Tag(TagId(2), "Noir", Instant.DISTANT_PAST)),
          onRemoveTag = {},
          onCreateTag = {},
          onAttachTag = {},
          onNavigateToManageTags = {},
          onDone = {},
        )
      }
    }
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
