package black.bracken.amenouzume.feature.addcollection

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.add_collection_field_category
import amenouzume.composeapp.generated.resources.add_collection_field_title
import amenouzume.composeapp.generated.resources.add_collection_submit
import amenouzume.composeapp.generated.resources.add_collection_title
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.uishared.bottombar.VaultBottomBar
import black.bracken.amenouzume.uishared.bottombar.VaultTab
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AddCollectionCoordinator(
  vaultPath: String,
  viewModel: AddCollectionViewModel = metroViewModel(),
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  val action = AddCollectionUiAction(
    onUpdateTitle = viewModel::onUpdateTitle,
    onUpdateCategory = viewModel::onUpdateCategory,
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
  Scaffold(
    topBar = {
      TopAppBar(title = { Text(stringResource(Res.string.add_collection_title)) })
    },
    bottomBar = {
      VaultBottomBar(
        selectedTab = VaultTab.ADD,
        onSelectTab = { tab ->
          if (tab == VaultTab.COLLECTIONS) action.onNavigateToCollections()
        },
      )
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      OutlinedTextField(
        value = state.title,
        onValueChange = action.onUpdateTitle,
        label = { Text(stringResource(Res.string.add_collection_field_title)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
      )

      OutlinedTextField(
        value = state.category,
        onValueChange = action.onUpdateCategory,
        label = { Text(stringResource(Res.string.add_collection_field_category)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
      )

      Button(
        onClick = action.onSubmit,
        enabled = !state.isBusy && state.title.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(stringResource(Res.string.add_collection_submit))
      }

      if (state.errorMessage != null) {
        Text(text = stringResource(state.errorMessage))
      }
    }
  }
}

@Preview
@Composable
private fun AddCollectionScreenPreview() {
  AddCollectionScreen(
    state = AddCollectionUiState(title = "", category = "", isBusy = false, errorMessage = null),
    action = AddCollectionUiAction.Noop,
  )
}

