package black.bracken.amenouzume.feature.opendatabase

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.open_database_browse_files
import amenouzume.composeapp.generated.resources.open_database_import_description
import amenouzume.composeapp.generated.resources.open_database_import_title
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.platform.launcher.rememberDirectoryPickerLauncher
import black.bracken.amenouzume.platform.launcher.rememberFilePickerLauncher
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OpenDatabaseCoordinator(viewModel: OpenDatabaseViewModel = koinViewModel()) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  val directoryLauncher = rememberDirectoryPickerLauncher { path ->
    path?.let { viewModel.createVault(it) }
  }
  val fileLauncher = rememberFilePickerLauncher { path ->
    path?.let { viewModel.openVault(it) }
  }
  OpenDatabaseScreen(
    state = state.value,
    onCreateDatabase = directoryLauncher,
    onBrowseFiles = fileLauncher,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OpenDatabaseScreen(
  state: OpenDatabaseUiState,
  onCreateDatabase: () -> Unit,
  onBrowseFiles: () -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val isLoading = state is OpenDatabaseUiState.Loaded && state.isLoading
  val errorMessage = (state as? OpenDatabaseUiState.Loaded)?.errorMessage

  val errorText = errorMessage?.let { stringResource(it) }
  LaunchedEffect(errorText) {
    errorText?.let { snackbarHostState.showSnackbar(it) }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = {}, enabled = !isLoading) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
        },
        title = { Text(stringResource(Res.string.open_database_title)) },
        actions = {
          IconButton(onClick = {}, enabled = !isLoading) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
          }
        },
      )
    },
    floatingActionButton = {
      FloatingActionButton(onClick = { if (!isLoading) onCreateDatabase() }) {
        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp,
            color = LocalContentColor.current,
          )
        } else {
          Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
      }
    },
  ) { innerPadding ->
    when (state) {
      OpenDatabaseUiState.Idle ->
        Box(
          modifier =
            Modifier
              .fillMaxSize()
              .padding(innerPadding),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }

      is OpenDatabaseUiState.Loaded ->
        DatabaseListContent(
          databases = state.databases,
          isLoading = state.isLoading,
          onBrowseFiles = onBrowseFiles,
          modifier = Modifier.padding(innerPadding),
        )
    }
  }
}

@Composable
private fun DatabaseListContent(
  databases: List<OpenDatabaseEntry>,
  isLoading: Boolean,
  onBrowseFiles: () -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    item { ImportLocalDatabaseCard(enabled = !isLoading, onBrowseFiles = onBrowseFiles) }
    item {
      Text(
        text = stringResource(Res.string.open_database_section_local_databases),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
      )
    }
    items(databases) { entry ->
      DatabaseEntryItem(entry = entry)
    }
  }
}

@Composable
private fun ImportLocalDatabaseCard(
  enabled: Boolean,
  onBrowseFiles: () -> Unit,
) {
  val primary = MaterialTheme.colorScheme.primary
  Box(
    modifier =
      Modifier
        .fillMaxWidth()
        .drawBehind {
          drawRoundRect(
            color = primary.copy(alpha = 0.2f),
            style =
              Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12.dp.toPx(), 8.dp.toPx())),
              ),
            cornerRadius = CornerRadius(24.dp.toPx()),
          )
        }.background(primary.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
        .padding(32.dp),
    contentAlignment = Alignment.Center,
  ) {
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
private fun DatabaseEntryItem(entry: OpenDatabaseEntry) {
  Card(
    onClick = {},
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
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
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
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
      Text(
        text = entry.size,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
