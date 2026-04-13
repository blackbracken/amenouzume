package black.bracken.amenouzume.feature.collectionlist

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.collection_list_filter
import amenouzume.composeapp.generated.resources.collection_list_title
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import black.bracken.amenouzume.uishared.component.VaultBottomBar
import black.bracken.amenouzume.uishared.component.VaultTab
import black.bracken.amenouzume.util.Loadable
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.size.Size
import black.bracken.amenouzume.util.resolvePixelSize
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun CollectionListCoordinator(
  vaultPath: String,
  viewModel: CollectionListViewModel = metroViewModel(),
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  val action = CollectionListUiAction(
    onNavigateToAdd = { viewModel.onNavigateToAdd(vaultPath) },
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
    bottomBar = {
      VaultBottomBar(
        selectedTab = VaultTab.COLLECTIONS,
        onSelectTab = { tab ->
          if (tab == VaultTab.ADD) action.onNavigateToAdd()
        },
      )
    },
  ) { innerPadding ->
    CollectionGridContent(
      collections = state.collections,
      modifier = Modifier.padding(innerPadding),
    )
  }
}

@Composable
private fun CollectionGridContent(
  collections: Loadable<List<CollectionListEntry>>,
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
        items(collections.value, key = { it.id }) { entry ->
          CollectionItem(entry = entry)
        }
      }
      is Loadable.Failed -> {}
    }
  }
}

private val THUMBNAIL_SIZE = 96.dp

@Composable
private fun CollectionItem(entry: CollectionListEntry) {
  Box(
    modifier = Modifier
      .aspectRatio(1f)
      .background(MaterialTheme.colorScheme.surfaceVariant),
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

@Preview
@Composable
private fun CollectionListScreenPreview() {
  CollectionListScreen(
    state = CollectionListUiState(isBusy = false, collections = Loadable.Loaded(emptyList())),
    action = CollectionListUiAction.Noop,
  )
}
