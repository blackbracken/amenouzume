package black.bracken.amenouzume.feature.collectionviewer

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.collection_viewer_file_count
import amenouzume.composeapp.generated.resources.collection_viewer_tags
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.kernel.model.Author
import black.bracken.amenouzume.kernel.model.CollectionId
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.platform.image.pathToCoilModel
import black.bracken.amenouzume.util.Loadable
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun CollectionViewerCoordinator(
  collectionId: CollectionId,
  viewModel: CollectionViewerViewModel =
    assistedMetroViewModel<CollectionViewerViewModel, CollectionViewerViewModel.Factory> {
      create(collectionId)
    },
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  val action = CollectionViewerUiAction(
    onClose = viewModel::onClose,
    onConsumeError = viewModel::onConsumeError,
    onTagClick = viewModel::onTagClick,
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
  val snackbarHostState = remember { SnackbarHostState() }
  val errorText = state.errorMessage?.let { stringResource(it) }

  LaunchedEffect(errorText) {
    errorText?.let {
      snackbarHostState.showSnackbar(it)
      action.onConsumeError()
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
        title = {},
      )
    },
  ) { innerPadding ->
    when (val content = state.content) {
      is Loadable.Loading -> {
        CollectionViewerSkeleton(modifier = Modifier.padding(innerPadding))
      }
      is Loadable.Failed -> {}

      is Loadable.Loaded -> {
        CollectionViewerContent(
          content = content.value,
          onTagClick = action.onTagClick,
          modifier = Modifier.padding(innerPadding),
        )
      }
    }
  }
}

@Composable
private fun CollectionViewerContent(
  content: CollectionViewerUiState.Content,
  onTagClick: (Tag) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxSize()) {
    PrimaryFileArea(filePath = content.primaryFilePath)

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .verticalScroll(rememberScrollState()),
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      CollectionHeader(
        title = content.title,
        fileCount = content.fileCount,
        categoryName = stringResource(content.category.labelRes),
      )

      if (content.authors.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        AuthorsSection(authors = content.authors)
        HorizontalDivider()
      }

      if (content.tags.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        TagsSection(tags = content.tags, onTagClick = onTagClick)
      }
    }
  }
}

@Composable
private fun PrimaryFileArea(filePath: String?) {
  if (filePath != null) {
    AsyncImage(
      model = ImageRequest.Builder(LocalPlatformContext.current)
        .data(pathToCoilModel(filePath))
        .build(),
      contentDescription = null,
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surfaceVariant),
      contentScale = ContentScale.FillWidth,
    )
  }
}

@Composable
private fun CollectionHeader(
  title: String,
  fileCount: Int,
  categoryName: String,
) {
  Column(modifier = Modifier.padding(horizontal = 16.dp)) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Default.Folder,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "$categoryName · ${stringResource(Res.string.collection_viewer_file_count, fileCount)}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun AuthorsSection(authors: List<Author>) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Default.People,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = authors.joinToString { it.primaryName },
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@Composable
private fun CollectionViewerSkeleton(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxSize()) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(MaterialTheme.colorScheme.surfaceVariant),
    )

    Column(modifier = Modifier.padding(16.dp)) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.6f)
          .height(24.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant),
      )
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth(0.35f)
          .height(16.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant),
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsSection(
  tags: List<Tag>,
  onTagClick: (Tag) -> Unit,
) {
  Column(modifier = Modifier.padding(horizontal = 16.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Default.Numbers,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = stringResource(Res.string.collection_viewer_tags),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Spacer(modifier = Modifier.height(8.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      tags.forEach { tag ->
        SuggestionChip(
          onClick = { onTagClick(tag) },
          label = {
            Text(
              text = tag.primaryName,
              style = MaterialTheme.typography.labelSmall,
            )
          },
        )
      }
    }
  }
}
