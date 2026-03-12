package black.bracken.amenouzume.feature.collectionlist

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.collection_list_filter
import amenouzume.composeapp.generated.resources.collection_list_tab_add
import amenouzume.composeapp.generated.resources.collection_list_tab_collections
import amenouzume.composeapp.generated.resources.collection_list_title
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.uishared.navigation.LocalNavigator
import black.bracken.amenouzume.uishared.navigation.Navigator
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun CollectionListCoordinator(
  viewModel: CollectionListViewModel = metroViewModel(),
  navigator: Navigator = LocalNavigator.current,
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  CollectionListScreen(
    state = state.value,
    onBack = navigator::back,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionListScreen(
  state: CollectionListUiState,
  onBack: () -> Unit,
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
      CollectionListBottomBar()
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

      is CollectionListUiState.Loaded ->
        LazyVerticalGrid(
          columns = GridCells.Fixed(3),
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

@Composable
private fun CollectionListBottomBar() {
  NavigationBar(modifier = Modifier.height(80.dp)) {
    NavigationBarItem(
      selected = true,
      onClick = {},
      icon = {
        Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = null)
      },
      label = {
        Text(
          text = stringResource(Res.string.collection_list_tab_collections),
          fontWeight = FontWeight.Bold,
        )
      },
      colors = NavigationBarItemDefaults.colors(
        indicatorColor = Color.Transparent,
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
      ),
    )
    NavigationBarItem(
      selected = false,
      onClick = {},
      icon = {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
      },
      label = { Text(stringResource(Res.string.collection_list_tab_add)) },
      colors = NavigationBarItemDefaults.colors(
        indicatorColor = Color.Transparent,
      ),
    )
  }
}
