package black.bracken.amenouzume.feature.addcollection

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.add_collection_authors
import amenouzume.composeapp.generated.resources.add_collection_browse_files
import amenouzume.composeapp.generated.resources.add_collection_field_title
import amenouzume.composeapp.generated.resources.add_collection_section_category
import amenouzume.composeapp.generated.resources.add_collection_submit
import amenouzume.composeapp.generated.resources.add_collection_tags
import amenouzume.composeapp.generated.resources.add_collection_title
import amenouzume.composeapp.generated.resources.add_collection_upload_title
import amenouzume.composeapp.generated.resources.category_fanzine
import amenouzume.composeapp.generated.resources.category_illustration
import amenouzume.composeapp.generated.resources.category_movie
import amenouzume.composeapp.generated.resources.category_photo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SwapVert
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.feature.addcollection.composable.SelectTagsBottomSheet
import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
import kotlin.time.Instant
import black.bracken.amenouzume.platform.haptic.AppHapticFeedbackType
import black.bracken.amenouzume.platform.haptic.LocalHapticFeedback
import black.bracken.amenouzume.platform.image.pathToCoilModel
import black.bracken.amenouzume.platform.launcher.LocalMultipleFilePickerLauncher
import black.bracken.amenouzume.uishared.component.DashedBorderArea
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.size.Size
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddCollectionCoordinator(
  vaultPath: String,
  viewModel: AddCollectionViewModel = metroViewModel(),
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  val mimeTypes = state.value.selectedCategory
    ?.acceptableMimeTypes
    .orEmpty()
  val rememberFilePicker = LocalMultipleFilePickerLauncher.current
  val filePickerLauncher = rememberFilePicker(mimeTypes, viewModel::onAddFiles)
  val action = AddCollectionUiAction(
    onClose = viewModel::onClose,
    onSelectCategory = viewModel::onSelectCategory,
    onAddFiles = filePickerLauncher,
    onUpdateTitle = viewModel::onUpdateTitle,
    onUpdateTagSearchQuery = viewModel::onUpdateTagSearchQuery,
    onToggleTag = viewModel::onToggleTag,
    onAttachTag = viewModel::onAttachTag,
    onCreateTag = viewModel::onCreateTag,
    onShowTagsSheet = viewModel::onShowTagsSheet,
    onDismissTagsSheet = viewModel::onDismissTagsSheet,
    onSubmit = viewModel::onCreateCollection,
    onNavigateToCollections = { viewModel.onNavigateToCollections(vaultPath) },
    onNavigateToEditOrder = {},
    onNavigateToManageTags = viewModel::onNavigateToManageTags,
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
  val snackbarHostState = remember { SnackbarHostState() }
  val errorText = state.errorMessage?.let { stringResource(it) }

  LaunchedEffect(errorText) {
    errorText?.let { snackbarHostState.showSnackbar(it) }
  }

  if (state.showTagsSheet) {
    val editing = state.editing
    if (editing != null) {
      SelectTagsBottomSheet(
        selectedTags = editing.tags,
        searchQuery = editing.tagSearchQuery,
        onSearchQueryChange = action.onUpdateTagSearchQuery,
        searchResultTags = editing.searchResultTags,
        recentTags = editing.recentTags,
        onToggleTag = action.onToggleTag,
        onAttachTag = action.onAttachTag,
        onCreateTag = action.onCreateTag,
        onNavigateToManageTags = action.onNavigateToManageTags,
        onDismiss = action.onDismissTagsSheet,
      )
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = action.onClose) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
        },
        title = { Text(stringResource(Res.string.add_collection_title)) },
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

      val editing = state.editing
      AnimatedVisibility(
        visible = editing != null,
        enter = fadeIn(),
      ) {
        requireNotNull(editing)

        Column {
          Spacer(modifier = Modifier.height(16.dp))

          AddFilesSection(
            filePaths = editing.filePaths,
            onAddFiles = action.onAddFiles,
            onNavigateToEditOrder = action.onNavigateToEditOrder,
          )

          AnimatedVisibility(
            visible = editing.filePaths.isNotEmpty(),
            enter = fadeIn(),
          ) {
            Column {
              Spacer(modifier = Modifier.height(16.dp))

              CollectionDetailsSection(
                title = editing.title,
                onUpdateTitle = action.onUpdateTitle,
                authors = editing.authors,
                tags = editing.tags,
                onTagsClick = action.onShowTagsSheet,
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
          }
        }
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
  Column(modifier = Modifier.padding(horizontal = 16.dp)) {
    Text(
      text = stringResource(Res.string.add_collection_section_category),
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurface,
      fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(2.dp))
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
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
  val haptic = LocalHapticFeedback.current()
  FilterChip(
    selected = selectedCategory == category,
    onClick = {
      haptic(AppHapticFeedbackType.LightTap)
      onSelectCategory(category)
    },
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
private fun AddFilesSection(
  filePaths: List<String>,
  onAddFiles: () -> Unit,
  onNavigateToEditOrder: () -> Unit,
) {
  val haptic = LocalHapticFeedback.current()
  Column {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      DashedBorderArea {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.AddPhotoAlternate,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp),
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = stringResource(Res.string.add_collection_upload_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
          )
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              haptic(AppHapticFeedbackType.LightTap)
              onAddFiles()
            },
            shape = CircleShape,
          ) {
            Text(
              text = stringResource(Res.string.add_collection_browse_files),
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    AnimatedVisibility(filePaths.isNotEmpty()) {
      FileCarousel(
        filePaths = filePaths,
        onAddFiles = onAddFiles,
      )
    }
  }
}

@Composable
private fun FileCarousel(
  filePaths: List<String>,
  onAddFiles: () -> Unit,
) {
  val thumbnailSizePx = 72
  val thumbnailSizeDp = thumbnailSizePx.dp
  val thumbnailShape = RoundedCornerShape(12.dp)

  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(filePaths) { path ->
      Box(
        modifier = Modifier
          .size(thumbnailSizeDp)
          .clip(thumbnailShape)
          .background(MaterialTheme.colorScheme.surfaceVariant),
      ) {
        AsyncImage(
          model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(pathToCoilModel(path))
            .size(Size(3 * thumbnailSizePx, 3 * thumbnailSizePx))
            .build(),
          contentDescription = null,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
        )
      }
    }
    item {
      Box(
        modifier = Modifier
          .size(thumbnailSizeDp)
          .clip(thumbnailShape)
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .clickable(onClick = onAddFiles),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Default.SwapVert,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun CollectionDetailsSection(
  title: String,
  onUpdateTitle: (String) -> Unit,
  authors: List<String>,
  tags: List<Tag>,
  onTagsClick: () -> Unit,
) {
  Column {
    Text(
      text = stringResource(Res.string.add_collection_field_title),
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(horizontal = 16.dp),
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
      value = title,
      onValueChange = onUpdateTitle,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      singleLine = true,
      shape = MaterialTheme.shapes.small,
      colors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
      ),
    )

    Spacer(modifier = Modifier.height(24.dp))
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
      icon = { Icon(imageVector = Icons.Filled.Numbers, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
      label = stringResource(Res.string.add_collection_tags),
      onClick = onTagsClick,
      trailing = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          tags.forEach { tag ->
            SuggestionChip(
              onClick = {},
              label = { Text(tag.primaryName.uppercase(), style = MaterialTheme.typography.labelSmall) },
            )
            Spacer(modifier = Modifier.width(4.dp))
          }
          Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      },
    )

    HorizontalDivider()
  }
}

@Composable
private fun DetailRow(
  icon: @Composable () -> Unit,
  label: String,
  onClick: (() -> Unit)? = null,
  trailing: @Composable () -> Unit,
) {
  val haptic = LocalHapticFeedback.current()
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .then(
        if (onClick != null) {
          Modifier.clickable {
            haptic(AppHapticFeedbackType.LightTap)
            onClick()
          }
        } else {
          Modifier
        },
      )
      .padding(
        horizontal = 16.dp,
        vertical = 16.dp,
      ),
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
    style = MaterialTheme.typography.titleSmall,
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
        editing = AddCollectionUiState.Editing(
          title = "",
          filePaths = listOf("/path/to/image1.png", "/path/to/image2.png"),
          authors = listOf("@jdoe_art"),
          tags = listOf(Tag(TagId(1), "Cyberpunk", Instant.DISTANT_PAST), Tag(TagId(2), "Noir", Instant.DISTANT_PAST)),
          tagSearchQuery = "",
          availableTags = listOf(
            Tag(TagId(3), "Architecture", Instant.DISTANT_PAST),
            Tag(TagId(4), "Design", Instant.DISTANT_PAST),
            Tag(TagId(5), "Engineering", Instant.DISTANT_PAST),
            Tag(TagId(6), "Marketing", Instant.DISTANT_PAST),
            Tag(TagId(7), "Photography", Instant.DISTANT_PAST),
            Tag(TagId(8), "UI/UX", Instant.DISTANT_PAST),
          ),
          searchResultTags = emptyList(),
          recentTags = listOf(Tag(TagId(7), "Photography", Instant.DISTANT_PAST), Tag(TagId(8), "UI/UX", Instant.DISTANT_PAST), Tag(TagId(6), "Marketing", Instant.DISTANT_PAST)),
        ),
        errorMessage = null,
        showTagsSheet = false,
      ),
      action = AddCollectionUiAction.Noop,
    )
  }
}
