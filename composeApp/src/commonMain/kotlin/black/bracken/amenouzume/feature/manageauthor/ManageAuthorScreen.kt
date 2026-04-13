package black.bracken.amenouzume.feature.manageauthor

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.manage_authors_create
import amenouzume.composeapp.generated.resources.manage_authors_search_placeholder
import amenouzume.composeapp.generated.resources.manage_authors_section_all
import amenouzume.composeapp.generated.resources.manage_authors_title
import amenouzume.composeapp.generated.resources.manage_authors_total
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.feature.manageauthor.composable.EditAuthorBottomSheet
import black.bracken.amenouzume.kernel.model.Author
import black.bracken.amenouzume.kernel.model.AuthorId
import black.bracken.amenouzume.platform.haptic.AppHapticFeedbackType
import black.bracken.amenouzume.platform.haptic.LocalHapticFeedback
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import black.bracken.amenouzume.util.Loadable
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlin.time.Instant
import org.jetbrains.compose.resources.stringResource

@Composable
fun ManageAuthorCoordinator(
  viewModel: ManageAuthorViewModel = metroViewModel(),
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  val action = ManageAuthorUiAction(
    onClose = viewModel::onClose,
    onUpdateSearchQuery = viewModel::onUpdateSearchQuery,
    onDeleteAuthor = viewModel::onDeleteAuthor,
    onCreateAuthor = viewModel::onCreateAuthor,
    onShowEditAuthorSheet = viewModel::onShowEditAuthorSheet,
    onDismissEditAuthorSheet = viewModel::onDismissEditAuthorSheet,
    onUpdateEditingPrimaryName = viewModel::onUpdateEditingPrimaryName,
    onUpdateEditingNewAliasInput = viewModel::onUpdateEditingNewAliasInput,
    onAddAlias = viewModel::onAddAlias,
    onRemoveAlias = viewModel::onRemoveAlias,
    onConsumeError = viewModel::onConsumeError,
  )
  ManageAuthorScreen(
    state = state.value,
    action = action,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ManageAuthorScreen(
  state: ManageAuthorUiState,
  action: ManageAuthorUiAction,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val errorText = state.errorMessage?.let { stringResource(it) }

  LaunchedEffect(errorText) {
    errorText?.let {
      snackbarHostState.showSnackbar(it)
      action.onConsumeError()
    }
  }

  if (state.editingAuthor != null) {
    EditAuthorBottomSheet(
      editingAuthor = state.editingAuthor,
      onUpdatePrimaryName = action.onUpdateEditingPrimaryName,
      onUpdateNewAliasInput = action.onUpdateEditingNewAliasInput,
      onAddAlias = action.onAddAlias,
      onRemoveAlias = action.onRemoveAlias,
      onDismiss = action.onDismissEditAuthorSheet,
    )
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
        title = { Text(stringResource(Res.string.manage_authors_title)) },
      )
    },
  ) { innerPadding ->
    val showCreateAuthor = state.searchQuery.isNotBlank() &&
      state.searchResultAuthors.none { it.primaryName.equals(state.searchQuery.trim(), ignoreCase = true) }
    val showSearchResults = state.searchResultAuthors.isNotEmpty()
    val showSearchSection = showCreateAuthor || showSearchResults

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) {
      item(key = "search_field") {
        SearchField(
          query = state.searchQuery,
          onQueryChange = action.onUpdateSearchQuery,
          modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
      }

      if (showSearchSection) {
        if (showCreateAuthor) {
          val trimmedQuery = state.searchQuery.trim()

          item(key = "create_author") {
            HorizontalDivider()
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { action.onCreateAuthor(trimmedQuery) }
                .padding(horizontal = 16.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
              )
              Spacer(modifier = Modifier.width(16.dp))
              Text(
                text = stringResource(Res.string.manage_authors_create, trimmedQuery),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
              )
            }
          }
        }

        if (showSearchResults) {
          items(state.searchResultAuthors, key = { "search_${it.id.value}" }) { author ->
            HorizontalDivider()
            AuthorRow(
              author = author,
              onEdit = { action.onShowEditAuthorSheet(author) },
              onDelete = { action.onDeleteAuthor(author) },
            )
          }
        }

        item(key = "search_divider") {
          HorizontalDivider()
          Spacer(modifier = Modifier.height(8.dp))
        }
      }

      item(key = "all_authors_header") {
        val allAuthors = state.authors
        AllAuthorsSectionHeader(
          totalCount = when (allAuthors) {
            is Loadable.Loaded -> allAuthors.value.size
            else -> null
          },
        )
      }

      when (val allAuthors = state.authors) {
        is Loadable.Loaded -> {
          items(allAuthors.value, key = { it.id.value }) { author ->
            HorizontalDivider()
            AuthorRow(
              author = author,
              onEdit = { action.onShowEditAuthorSheet(author) },
              onDelete = { action.onDeleteAuthor(author) },
            )
          }

          if (allAuthors.value.isNotEmpty()) {
            item(key = "bottom_divider") {
              HorizontalDivider()
            }
          }
        }

        else -> {}
      }
    }
  }
}

@Composable
private fun SearchField(
  query: String,
  onQueryChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    placeholder = { Text(stringResource(Res.string.manage_authors_search_placeholder)) },
    modifier = modifier.fillMaxWidth(),
    singleLine = true,
    leadingIcon = {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    },
    shape = MaterialTheme.shapes.small,
    colors = OutlinedTextFieldDefaults.colors(
      unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    ),
  )
}

@Composable
private fun AllAuthorsSectionHeader(totalCount: Int?) {
  Row(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = stringResource(Res.string.manage_authors_section_all),
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.primary,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.weight(1f),
    )

    AnimatedVisibility(visible = totalCount != null) {
      if (totalCount != null) {
        Text(
          text = stringResource(Res.string.manage_authors_total, totalCount),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium,
        )
      }
    }
  }
}

@Composable
private fun AuthorRow(
  author: Author,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
) {
  val haptic = LocalHapticFeedback.current()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Default.Person,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
      text = author.primaryName,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.weight(1f),
    )
    IconButton(
      onClick = {
        haptic(AppHapticFeedbackType.LightTap)
        onEdit()
      },
    ) {
      Icon(
        imageVector = Icons.Default.Edit,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    IconButton(
      onClick = {
        haptic(AppHapticFeedbackType.LightTap)
        onDelete()
      },
    ) {
      Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Preview
@Composable
private fun ManageAuthorScreenPreview() {
  AmenouzumeTheme {
    ManageAuthorScreen(
      state = ManageAuthorUiState(
        authors = Loadable.Loaded(
          listOf(
            Author(AuthorId(1), "John Doe", Instant.DISTANT_PAST),
            Author(AuthorId(2), "Jane Smith", Instant.DISTANT_PAST),
            Author(AuthorId(3), "Bob Ross", Instant.DISTANT_PAST),
          ),
        ),
        searchQuery = "",
      ),
      action = ManageAuthorUiAction.Noop,
    )
  }
}
