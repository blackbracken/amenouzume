package black.bracken.amenouzume.feature.collectionviewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.kernel.model.CollectionId
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
fun CollectionViewerCoordinator(
  collectionId: CollectionId,
  viewModel: CollectionViewerViewModel =
    assistedMetroViewModel<CollectionViewerViewModel, CollectionViewerViewModel.Factory> { create(collectionId) },
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  val action = CollectionViewerUiAction(
    onClose = viewModel::onClose,
  )
  CollectionViewerScreen(
    state = state.value,
    action = action,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CollectionViewerScreen(
  state: CollectionViewerUiState,
  action: CollectionViewerUiAction,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = action.onClose) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
        },
        title = {},
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.Center,
    ) {
      Text("Collection Viewer")
    }
  }
}
