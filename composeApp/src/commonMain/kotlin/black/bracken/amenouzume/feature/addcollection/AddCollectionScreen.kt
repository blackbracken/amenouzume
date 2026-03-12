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
import black.bracken.amenouzume.uishared.navigation.CollectionListRoute
import black.bracken.amenouzume.uishared.navigation.LocalNavigator
import black.bracken.amenouzume.uishared.navigation.Navigator
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddCollectionCoordinator(
  vaultPath: String,
  viewModel: AddCollectionViewModel = metroViewModel(),
  navigator: Navigator = LocalNavigator.current,
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  AddCollectionScreen(
    state = state.value,
    onUpdateTitle = viewModel::updateTitle,
    onUpdateCategory = viewModel::updateCategory,
    onSubmit = viewModel::addCollection,
    onNavigateToCollections = { navigator.navigate(CollectionListRoute(vaultPath)) },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCollectionScreen(
  state: AddCollectionUiState,
  onUpdateTitle: (String) -> Unit,
  onUpdateCategory: (String) -> Unit,
  onSubmit: () -> Unit,
  onNavigateToCollections: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(title = { Text(stringResource(Res.string.add_collection_title)) })
    },
    bottomBar = {
      VaultBottomBar(
        selectedTab = VaultTab.ADD,
        onSelectTab = { tab ->
          if (tab == VaultTab.COLLECTIONS) onNavigateToCollections()
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
        onValueChange = onUpdateTitle,
        label = { Text(stringResource(Res.string.add_collection_field_title)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
      )

      OutlinedTextField(
        value = state.category,
        onValueChange = onUpdateCategory,
        label = { Text(stringResource(Res.string.add_collection_field_category)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
      )

      Button(
        onClick = onSubmit,
        enabled = !state.isLoading && state.title.isNotBlank(),
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
