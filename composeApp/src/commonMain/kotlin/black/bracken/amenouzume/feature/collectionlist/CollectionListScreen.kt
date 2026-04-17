package black.bracken.amenouzume.feature.collectionlist

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.collection_list_filter
import amenouzume.composeapp.generated.resources.collection_list_title
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import black.bracken.amenouzume.kernel.model.CollectionId
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.platform.haptic.AppHapticFeedbackType
import black.bracken.amenouzume.platform.haptic.LocalHapticFeedback
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.resolvePixelSize
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.size.Size
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun CollectionListCoordinator(
  vaultPath: String,
  filterTagId: TagId?,
  showAddFab: Boolean,
  viewModel: CollectionListViewModel =
    assistedMetroViewModel<CollectionListViewModel, CollectionListViewModel.Factory> {
      create(filterTagId, showAddFab)
    },
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  val action = CollectionListUiAction(
    onNavigateToAdd = { viewModel.onNavigateToAdd(vaultPath) },
    onOpenCollection = { id -> viewModel.onOpenCollection(vaultPath, id) },
  )
  CollectionListScreen(
    state = state.value,
    action = action,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CollectionListScreen(
  state: CollectionListUiState,
  action: CollectionListUiAction,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(stringResource(Res.string.collection_list_title)) },
        actions = {
          TextButton(onClick = {}) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
            )
            Text(
              text = stringResource(Res.string.collection_list_filter),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(start = 4.dp),
            )
          }
        },
      )
    },
    floatingActionButton = {
      if (state.showAddFab) {
        val haptic = LocalHapticFeedback.current()
        FloatingActionButton(
          onClick = {
            haptic(AppHapticFeedbackType.LightTap)
            action.onNavigateToAdd()
          },
          modifier = Modifier.testTag(CollectionListTestTags.AddFab),
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
      }
    },
  ) { innerPadding ->
    Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
      if (state.filterTag != null) {
        ActiveFiltersSection(filterTag = state.filterTag)
      }

      CollectionGridContent(
        collections = state.collections,
        onOpenCollection = action.onOpenCollection,
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveFiltersSection(filterTag: Tag) {
  FlowRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    SuggestionChip(
      onClick = {},
      icon = {
        Icon(
          imageVector = Icons.Default.Numbers,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      },
      label = {
        Text(
          text = filterTag.primaryName,
          style = MaterialTheme.typography.labelSmall,
        )
      },
    )
  }
}

@Composable
private fun CollectionGridContent(
  collections: Loadable<List<CollectionListEntry>>,
  onOpenCollection: (CollectionId) -> Unit,
  modifier: Modifier = Modifier,
) {
  val minWidthDp = currentWindowAdaptiveInfo().windowSizeClass.minWidthDp
  val columns = when {
    minWidthDp < WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND -> 3
    minWidthDp < WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND -> 5
    else -> 7
  }

  LazyVerticalGrid(
    columns = GridCells.Fixed(columns),
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = 64.dp),
  ) {
    when (collections) {
      is Loadable.Loading -> {
        items(columns * 3) {
          CollectionItemSkeleton()
        }
      }
      is Loadable.Loaded -> {
        items(collections.value, key = { it.id.value }) { entry ->
          CollectionItem(entry = entry, onClick = { onOpenCollection(entry.id) })
        }
      }
      is Loadable.Failed -> {}
    }
  }
}

private val THUMBNAIL_SIZE = 96.dp

@Composable
private fun CollectionItem(
  entry: CollectionListEntry,
  onClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .aspectRatio(1f)
      .background(MaterialTheme.colorScheme.surfaceVariant)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    if (entry.thumbnailPath != null) {
      AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
          .data(entry.thumbnailPath)
          .size(Size(resolvePixelSize(THUMBNAIL_SIZE), resolvePixelSize(THUMBNAIL_SIZE)))
          .build(),
        contentDescription = entry.title,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
      )
    } else {
      Text(
        text = entry.title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun CollectionItemSkeleton() {
  Box(
    modifier = Modifier
      .aspectRatio(1f)
      .background(MaterialTheme.colorScheme.surfaceVariant),
  )
}

object CollectionListTestTags {
  const val AddFab = "collection_list_add_fab"
}

@Preview
@Composable
private fun CollectionListScreenPreview() {
  CollectionListScreen(
    state = CollectionListUiState(
      isBusy = false,
      collections = Loadable.Loaded(emptyList()),
      filterTag = null,
      showAddFab = true,
    ),
    action = CollectionListUiAction.Noop,
  )
}
