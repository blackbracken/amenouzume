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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.uishared.bottombar.VaultBottomBar
import black.bracken.amenouzume.uishared.bottombar.VaultTab
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun CollectionListCoordinator(
  vaultPath: String,
  viewModel: CollectionListViewModel = metroViewModel(),
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  CollectionListScreen(
    state = state.value,
    onBack = viewModel::onBack,
    onNavigateToAdd = { viewModel.onNavigateToAdd(vaultPath) },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionListScreen(
  state: CollectionListUiState,
  onBack: () -> Unit,
  onNavigateToAdd: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = null,
            )
          }
        },
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
          if (tab == VaultTab.ADD) onNavigateToAdd()
        },
      )
    },
  ) { innerPadding ->
    when (state) {
      CollectionListUiState.Idle ->
        Box(
          modifier = Modifier.fillMaxSize().padding(innerPadding),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }

      is CollectionListUiState.Loaded -> {
        val minWidthDp = currentWindowAdaptiveInfo().windowSizeClass.minWidthDp
        val columns = when {
          minWidthDp < WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND -> 3
          minWidthDp < WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND -> 5
          else -> 7
        }

        LazyVerticalGrid(
          columns = GridCells.Fixed(columns),
          modifier = Modifier.fillMaxSize().padding(innerPadding),
          contentPadding = PaddingValues(bottom = 64.dp),
        ) {
          items(state.collections) { entry ->
            Box(
              modifier = Modifier
                .aspectRatio(1f)
                .background(Color(entry.color.toInt())),
            )
          }
        }
      }
    }
  }
}
