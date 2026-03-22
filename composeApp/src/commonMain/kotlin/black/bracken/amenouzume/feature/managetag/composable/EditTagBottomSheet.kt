package black.bracken.amenouzume.feature.managetag.composable

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.edit_tag_add_alias
import amenouzume.composeapp.generated.resources.edit_tag_aliases
import amenouzume.composeapp.generated.resources.edit_tag_done
import amenouzume.composeapp.generated.resources.edit_tag_new_alias_placeholder
import amenouzume.composeapp.generated.resources.edit_tag_primary_name
import amenouzume.composeapp.generated.resources.edit_tag_title
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import black.bracken.amenouzume.feature.managetag.ManageTagUiState
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditTagBottomSheet(
  editingTag: ManageTagUiState.EditingTag,
  onUpdatePrimaryName: (String) -> Unit,
  onUpdateNewAliasInput: (String) -> Unit,
  onAddAlias: () -> Unit,
  onRemoveAlias: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
  ) {
    EditTagContent(
      editingTag = editingTag,
      onUpdatePrimaryName = onUpdatePrimaryName,
      onUpdateNewAliasInput = onUpdateNewAliasInput,
      onAddAlias = onAddAlias,
      onRemoveAlias = onRemoveAlias,
      onDone = onDismiss,
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ColumnScope.EditTagContent(
  editingTag: ManageTagUiState.EditingTag,
  onUpdatePrimaryName: (String) -> Unit,
  onUpdateNewAliasInput: (String) -> Unit,
  onAddAlias: () -> Unit,
  onRemoveAlias: (String) -> Unit,
  onDone: () -> Unit,
) {
  LazyColumn(modifier = Modifier.weight(1f)) {
    item(key = "header") {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Numbers,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(Res.string.edit_tag_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
          )
        }

        Spacer(modifier = Modifier.height(20.dp))
      }
    }

    item(key = "primary_name") {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = stringResource(Res.string.edit_tag_primary_name),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = editingTag.pendingPrimaryName,
          onValueChange = onUpdatePrimaryName,
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
        Spacer(modifier = Modifier.height(20.dp))
      }
    }

    item(key = "aliases_header") {
      HorizontalDivider()
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = stringResource(Res.string.edit_tag_aliases),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
      }
    }

    item(key = "aliases_chips") {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        if (editingTag.pendingAliasNames.isNotEmpty()) {
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy((-8).dp),
          ) {
            editingTag.pendingAliasNames.forEach { alias ->
              InputChip(
                selected = false,
                onClick = { onRemoveAlias(alias) },
                label = { Text(alias) },
                trailingIcon = {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                  )
                },
                colors = InputChipDefaults.inputChipColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    }

    item(key = "add_alias") {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        OutlinedTextField(
          value = editingTag.newAliasInput,
          onValueChange = onUpdateNewAliasInput,
          placeholder = { Text(stringResource(Res.string.edit_tag_new_alias_placeholder)) },
          modifier = Modifier.weight(1f),
          singleLine = true,
          shape = MaterialTheme.shapes.small,
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
          ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
          onClick = onAddAlias,
          enabled = editingTag.newAliasInput.isNotBlank(),
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(Res.string.edit_tag_add_alias),
            tint = if (editingTag.newAliasInput.isNotBlank()) {
              MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.onSurfaceVariant
            },
          )
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider()
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
      text = stringResource(Res.string.edit_tag_done),
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(vertical = 8.dp),
    )
  }
}

@Preview
@Composable
private fun EditTagContentPreview() {
  AmenouzumeTheme {
    Surface {
      Column {
        EditTagContent(
          editingTag = ManageTagUiState.EditingTag(
            tagId = TagId(1),
            initialPrimaryName = "Cyberpunk",
            initialAliases = emptyList(),
            pendingPrimaryName = "Cyberpunk",
            pendingAliasNames = listOf("サイバーパンク", "CP"),
            newAliasInput = "",
          ),
          onUpdatePrimaryName = {},
          onUpdateNewAliasInput = {},
          onAddAlias = {},
          onRemoveAlias = {},
          onDone = {},
        )
      }
    }
  }
}
