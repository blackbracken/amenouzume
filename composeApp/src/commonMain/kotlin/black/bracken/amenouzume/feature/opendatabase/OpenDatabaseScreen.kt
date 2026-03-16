package black.bracken.amenouzume.feature.opendatabase

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.open_database_browse_files
import amenouzume.composeapp.generated.resources.open_database_import_description
import amenouzume.composeapp.generated.resources.open_database_import_title
import amenouzume.composeapp.generated.resources.open_database_retry
import amenouzume.composeapp.generated.resources.open_database_section_local_databases
import amenouzume.composeapp.generated.resources.open_database_title
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.platform.launcher.rememberDirectoryPickerLauncher
import black.bracken.amenouzume.platform.launcher.rememberFilePickerLauncher
import black.bracken.amenouzume.uishared.component.DashedBorderArea
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import black.bracken.amenouzume.util.Loadable
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun OpenDatabaseCoordinator(viewModel: OpenDatabaseViewModel = metroViewModel()) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  val directoryLauncher = rememberDirectoryPickerLauncher { path ->
    path?.let { viewModel.onCreateVault(it) }
  }
  val fileLauncher = rememberFilePickerLauncher { path ->
    path?.let { viewModel.onOpenVault(it) }
  }
  val action = OpenDatabaseUiAction(
    onCreateDatabase = directoryLauncher,
    onBrowseFiles = fileLauncher,
    onRetry = viewModel::onRetry,
    onOpenEntry = viewModel::onOpenEntry,
    onDeleteEntry = viewModel::onDeleteEntry,
  )
  OpenDatabaseScreen(
    state = state.value,
    action = action,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OpenDatabaseScreen(
  state: OpenDatabaseUiState,
  action: OpenDatabaseUiAction,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val errorText = state.errorMessage?.let { stringResource(it) }

  LaunchedEffect(errorText) {
    errorText?.let { snackbarHostState.showSnackbar(it) }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = { Text(stringResource(Res.string.open_database_title)) },
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { if (!state.isBusy) action.onCreateDatabase() },
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
      }
    },
  ) { innerPadding ->
    DatabaseListContent(
      databases = state.databases,
      isBusy = state.isBusy,
      onBrowseFiles = action.onBrowseFiles,
      onRetry = action.onRetry,
      onOpenEntry = action.onOpenEntry,
      onDeleteEntry = action.onDeleteEntry,
      modifier = Modifier.padding(innerPadding),
    )
  }
}

@Composable
private fun DatabaseListContent(
  databases: Loadable<List<OpenDatabaseEntry>>,
  isBusy: Boolean,
  onBrowseFiles: () -> Unit,
  onRetry: () -> Unit,
  onOpenEntry: (OpenDatabaseEntry) -> Unit,
  onDeleteEntry: (OpenDatabaseEntry) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    item { ImportLocalDatabaseCard(enabled = !isBusy, onBrowseFiles = onBrowseFiles) }
    item {
      Text(
        text = stringResource(Res.string.open_database_section_local_databases),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
      )
    }
    when (databases) {
      is Loadable.Loading -> {
        items(3) { DatabaseEntryItemSkeleton() }
      }
      is Loadable.Loaded -> {
        items(databases.value.take(5)) { entry ->
          DatabaseEntryItem(entry = entry, onClick = { onOpenEntry(entry) }, onDelete = { onDeleteEntry(entry) })
        }
      }
      is Loadable.Failed -> {
        item {
          DatabaseLoadFailedItem(
            message = stringResource(databases.messageRes),
            onRetry = onRetry,
          )
        }
      }
    }
  }
}

@Composable
private fun ImportLocalDatabaseCard(
  enabled: Boolean,
  onBrowseFiles: () -> Unit,
) {
  val primary = MaterialTheme.colorScheme.primary
  DashedBorderArea {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Box(
        modifier =
          Modifier
            .size(64.dp)
            .background(primary.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Default.Upload,
          contentDescription = null,
          tint = primary,
          modifier = Modifier.size(32.dp),
        )
      }
      Spacer(Modifier.height(8.dp))
      Text(
        text = stringResource(Res.string.open_database_import_title),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
      )
      Text(
        text = stringResource(Res.string.open_database_import_description),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = 200.dp),
      )
      Spacer(Modifier.height(8.dp))
      Button(
        onClick = onBrowseFiles,
        shape = CircleShape,
        enabled = enabled,
      ) {
        Text(
          text = stringResource(Res.string.open_database_browse_files),
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
        )
      }
    }
  }
}

@Composable
private fun DatabaseEntryItem(
  entry: OpenDatabaseEntry,
  onClick: () -> Unit,
  onDelete: () -> Unit,
) {
  Card(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Row(
      modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Box(
        modifier =
          Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Default.Storage,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = entry.name,
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = entry.path,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
      IconButton(onClick = onDelete) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

@Composable
private fun DatabaseEntryItemSkeleton() {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Box(
        modifier =
          Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small),
      )
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Box(
          modifier =
            Modifier
              .fillMaxWidth(0.4f)
              .height(12.dp)
              .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.extraSmall),
        )
        Box(
          modifier =
            Modifier
              .fillMaxWidth(0.7f)
              .height(10.dp)
              .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.extraSmall),
        )
      }
    }
  }
}

@Composable
private fun DatabaseLoadFailedItem(
  message: String,
  onRetry: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier.fillMaxWidth(),
      )
      Button(
        onClick = onRetry,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.error,
          contentColor = MaterialTheme.colorScheme.onError,
        ),
      ) {
        Text(stringResource(Res.string.open_database_retry))
      }
    }
  }
}

@Preview
@Composable
private fun OpenDatabaseScreenPreview() =
  AmenouzumeTheme {
    OpenDatabaseScreen(
      state = OpenDatabaseUiState(isBusy = false, databases = Loadable.Loaded(emptyList()), errorMessage = null),
      action = OpenDatabaseUiAction.Noop,
    )
  }

@Preview
@Composable
private fun OpenDatabaseScreenLoadingPreview() =
  AmenouzumeTheme {
    OpenDatabaseScreen(
      state = OpenDatabaseUiState(isBusy = false, databases = Loadable.Loading, errorMessage = null),
      action = OpenDatabaseUiAction.Noop,
    )
  }
