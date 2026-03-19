package black.bracken.amenouzume.feature.addcollection.composable

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.select_tags_create
import amenouzume.composeapp.generated.resources.select_tags_manage
import amenouzume.composeapp.generated.resources.select_tags_recommended
import amenouzume.composeapp.generated.resources.select_tags_search_placeholder
import amenouzume.composeapp.generated.resources.select_tags_selected
import amenouzume.composeapp.generated.resources.select_tags_title
import amenouzume.composeapp.generated.resources.select_tags_update
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectTagsBottomSheet(
  selectedTags: List<Tag>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  availableTags: List<Tag>,
  recentTags: List<Tag>,
  onToggleTag: (Tag) -> Unit,
  onAttachTag: (Tag) -> Unit,
  onCreateTag: (String) -> Unit,
  onDismiss: () -> Unit,
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
      availableTags = availableTags,
      recentTags = recentTags,
      onToggleTag = onToggleTag,
      onRemoveTag = onToggleTag,
      onCreateTag = onCreateTag,
      onAttachTag = onAttachTag,
      onDone = onDismiss,
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ColumnScope.SelectTagsContent(
  selectedTags: List<Tag>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  availableTags: List<Tag>,
  recentTags: List<Tag>,
  onToggleTag: (Tag) -> Unit,
  onRemoveTag: (Tag) -> Unit,
  onCreateTag: (String) -> Unit,
  onAttachTag: (Tag) -> Unit,
  onDone: () -> Unit,
) {

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

    if (selectedTags.isNotEmpty()) {
      Spacer(modifier = Modifier.height(16.dp))
      SectionHeader(text = stringResource(Res.string.select_tags_selected))
      Spacer(modifier = Modifier.height(12.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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

    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
  }

  LazyColumn(modifier = Modifier.weight(1f)) {
    if (searchQuery.isNotBlank()) {
      val trimmedQuery = searchQuery.trim()
      item(key = "create_tag") {
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
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
      }
    }

    item(key = "section_header") {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(text = stringResource(Res.string.select_tags_recommended))
        Spacer(modifier = Modifier.height(12.dp))
      }
    }

    items(recentTags, key = { it.id.value }) { tag ->
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
      HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }

    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { }
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
          selectedTags = listOf(Tag(TagId(1), "Cyberpunk")),
          searchQuery = "Vaporw",
          onSearchQueryChange = {},
          availableTags = listOf(Tag(TagId(1), "Cyberpunk"), Tag(TagId(2), "Noir"), Tag(TagId(3), "Photography")),
          recentTags = listOf(Tag(TagId(3), "Photography"), Tag(TagId(2), "Noir")),
          onToggleTag = {},
          onRemoveTag = {},
          onCreateTag = {},
          onAttachTag = {},
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
