package black.bracken.amenouzume.feature.addcollection

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.add_collection_authors
import amenouzume.composeapp.generated.resources.add_collection_field_title
import amenouzume.composeapp.generated.resources.add_collection_field_title_placeholder
import amenouzume.composeapp.generated.resources.add_collection_public
import amenouzume.composeapp.generated.resources.add_collection_public_description
import amenouzume.composeapp.generated.resources.add_collection_save_draft
import amenouzume.composeapp.generated.resources.add_collection_section_category
import amenouzume.composeapp.generated.resources.add_collection_section_details
import amenouzume.composeapp.generated.resources.add_collection_section_upload
import amenouzume.composeapp.generated.resources.add_collection_submit
import amenouzume.composeapp.generated.resources.add_collection_tags
import amenouzume.composeapp.generated.resources.add_collection_title
import amenouzume.composeapp.generated.resources.add_collection_upload_description
import amenouzume.composeapp.generated.resources.add_collection_upload_title
import amenouzume.composeapp.generated.resources.category_fanzine
import amenouzume.composeapp.generated.resources.category_illustration
import amenouzume.composeapp.generated.resources.category_movie
import amenouzume.composeapp.generated.resources.category_photo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.feature.addcollection.composable.SelectTagsBottomSheet
import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
import black.bracken.amenouzume.uishared.component.DashedBorderArea
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddCollectionCoordinator(
  vaultPath: String,
  viewModel: AddCollectionViewModel = metroViewModel(),
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  val action = AddCollectionUiAction(
    onClose = viewModel::onClose,
    onSaveDraft = {},
    onSelectCategory = viewModel::onSelectCategory,
    onUploadArtwork = {},
    onUpdateTitle = viewModel::onUpdateTitle,
    onUpdateTags = viewModel::onUpdateTags,
    onAddTag = viewModel::onAddTag,
    onTogglePublic = viewModel::onTogglePublic,
    onSubmit = viewModel::onAddCollection,
    onNavigateToCollections = { viewModel.onNavigateToCollections(vaultPath) },
  )
  AddCollectionScreen(
    state = state.value,
    action = action,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddCollectionScreen(
  state: AddCollectionUiState,
  action: AddCollectionUiAction,
) {
  var showTagsSheet by rememberSaveable { mutableStateOf(false) }

  if (showTagsSheet) {
    val editing = state.editing
    if (editing is AddCollectionUiState.Editing.Illustration) {
      SelectTagsBottomSheet(
        selectedTags = editing.tags,
        availableTags = editing.availableTags,
        onUpdateTags = action.onUpdateTags,
        onAddTag = action.onAddTag,
        onDismiss = { showTagsSheet = false },
      )
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = action.onClose) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
        },
        title = { Text(stringResource(Res.string.add_collection_title)) },
        actions = {
          TextButton(onClick = action.onSaveDraft) {
            Text(
              text = stringResource(Res.string.add_collection_save_draft),
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold,
            )
          }
        },
      )
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState()),
    ) {
      CategorySection(
        selectedCategory = state.selectedCategory,
        onSelectCategory = action.onSelectCategory,
      )

      when (val editing = state.editing) {
        is AddCollectionUiState.Editing.Illustration -> {
          HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

          UploadArtworkSection(onUploadArtwork = action.onUploadArtwork)

          HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

          CollectionDetailsSection(
            title = editing.title,
            onUpdateTitle = action.onUpdateTitle,
            authors = editing.authors,
            tags = editing.tags,
            isPublic = editing.isPublic,
            onTogglePublic = action.onTogglePublic,
            onTagsClick = { showTagsSheet = true },
          )

          Spacer(modifier = Modifier.weight(1f))

          Button(
            onClick = action.onSubmit,
            enabled = !state.isBusy && editing.title.isNotBlank(),
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
              text = stringResource(Res.string.add_collection_submit),
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(vertical = 8.dp),
            )
          }
        }

        null -> {}
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategorySection(
  selectedCategory: CollectionCategory?,
  onSelectCategory: (CollectionCategory) -> Unit,
) {
  Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
    SectionHeader(text = stringResource(Res.string.add_collection_section_category))
    Spacer(modifier = Modifier.height(12.dp))
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      CategoryChip(CollectionCategory.ILLUSTRATION, Res.string.category_illustration, selectedCategory, onSelectCategory)
      CategoryChip(CollectionCategory.PHOTO, Res.string.category_photo, selectedCategory, onSelectCategory)
      CategoryChip(CollectionCategory.FANZINE, Res.string.category_fanzine, selectedCategory, onSelectCategory)
      CategoryChip(CollectionCategory.MOVIE, Res.string.category_movie, selectedCategory, onSelectCategory)
    }
  }
}

@Composable
private fun CategoryChip(
  category: CollectionCategory,
  labelRes: StringResource,
  selectedCategory: CollectionCategory?,
  onSelectCategory: (CollectionCategory) -> Unit,
) {
  FilterChip(
    selected = selectedCategory == category,
    onClick = { onSelectCategory(category) },
    label = {
      Text(
        text = stringResource(labelRes),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelMedium,
      )
    },
    colors = FilterChipDefaults.filterChipColors(
      selectedContainerColor = MaterialTheme.colorScheme.primary,
      selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
    ),
  )
}

@Composable
private fun UploadArtworkSection(onUploadArtwork: () -> Unit) {
  Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
    SectionHeader(text = stringResource(Res.string.add_collection_section_upload))
    Spacer(modifier = Modifier.height(12.dp))
    UploadDropArea(onUploadArtwork = onUploadArtwork)
  }
}

@Composable
private fun UploadDropArea(onUploadArtwork: () -> Unit) {
  DashedBorderArea {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
        imageVector = Icons.Default.AddPhotoAlternate,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp),
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = stringResource(Res.string.add_collection_upload_title),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = stringResource(Res.string.add_collection_upload_description),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
private fun CollectionDetailsSection(
  title: String,
  onUpdateTitle: (String) -> Unit,
  authors: List<String>,
  tags: List<String>,
  isPublic: Boolean,
  onTogglePublic: (Boolean) -> Unit,
  onTagsClick: () -> Unit,
) {
  Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
    SectionHeader(text = stringResource(Res.string.add_collection_section_details))
    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = stringResource(Res.string.add_collection_field_title),
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
      value = title,
      onValueChange = onUpdateTitle,
      placeholder = { Text(stringResource(Res.string.add_collection_field_title_placeholder)) },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
      shape = MaterialTheme.shapes.small,
      colors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
      ),
    )

    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()

    DetailRow(
      icon = { Icon(imageVector = Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
      label = stringResource(Res.string.add_collection_authors),
      trailing = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          authors.forEach { author ->
            SuggestionChip(
              onClick = {},
              label = { Text(author, style = MaterialTheme.typography.labelSmall) },
            )
            Spacer(modifier = Modifier.width(4.dp))
          }
          Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      },
    )

    HorizontalDivider()

    DetailRow(
      icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Label, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
      label = stringResource(Res.string.add_collection_tags),
      onClick = onTagsClick,
      trailing = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          tags.forEach { tag ->
            SuggestionChip(
              onClick = {},
              label = { Text(tag.uppercase(), style = MaterialTheme.typography.labelSmall) },
            )
            Spacer(modifier = Modifier.width(4.dp))
          }
          Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      },
    )

    HorizontalDivider()

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(modifier = Modifier.width(16.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(Res.string.add_collection_public),
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Medium,
        )
        Text(
          text = stringResource(Res.string.add_collection_public_description),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Switch(checked = isPublic, onCheckedChange = onTogglePublic)
    }
  }
}

@Composable
private fun DetailRow(
  icon: @Composable () -> Unit,
  label: String,
  onClick: (() -> Unit)? = null,
  trailing: @Composable () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
      .padding(vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    icon()
    Spacer(modifier = Modifier.width(16.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.Medium,
      modifier = Modifier.weight(1f),
    )
    trailing()
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

@Preview
@Composable
private fun AddCollectionScreenPreview() {
  AmenouzumeTheme {
    AddCollectionScreen(
      state = AddCollectionUiState(
        isBusy = false,
        selectedCategory = CollectionCategory.ILLUSTRATION,
        editing = AddCollectionUiState.Editing.Illustration(
          title = "",
          authors = listOf("@jdoe_art"),
          tags = listOf("Cyberpunk", "Noir"),
          availableTags = listOf("Architecture", "Design", "Engineering", "Marketing", "Photography", "UI/UX"),
          isPublic = true,
        ),
        errorMessage = null,
      ),
      action = AddCollectionUiAction.Noop,
    )
  }
}
