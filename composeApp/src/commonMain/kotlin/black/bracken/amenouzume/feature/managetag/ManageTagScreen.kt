package black.bracken.amenouzume.feature.managetag

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.manage_tags_create
import amenouzume.composeapp.generated.resources.manage_tags_search_placeholder
import amenouzume.composeapp.generated.resources.manage_tags_search_results
import amenouzume.composeapp.generated.resources.manage_tags_section_all
import amenouzume.composeapp.generated.resources.manage_tags_title
import amenouzume.composeapp.generated.resources.manage_tags_total
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.platform.haptic.AppHapticFeedbackType
import black.bracken.amenouzume.platform.haptic.rememberHapticFeedback
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import black.bracken.amenouzume.util.Loadable
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun ManageTagCoordinator(
  viewModel: ManageTagViewModel = metroViewModel(),
) {
  val state = viewModel.uiState.collectAsStateWithLifecycle()
  val action = ManageTagUiAction(
    onClose = viewModel::onClose,
    onUpdateSearchQuery = viewModel::onUpdateSearchQuery,
    onDeleteTag = viewModel::onDeleteTag,
    onCreateTag = viewModel::onCreateTag,
  )
  ManageTagScreen(
    state = state.value,
    action = action,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ManageTagScreen(
  state: ManageTagUiState,
  action: ManageTagUiAction,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = action.onClose) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
        },
        title = { Text(stringResource(Res.string.manage_tags_title)) },
      )
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) {
      SearchField(
        query = state.searchQuery,
        onQueryChange = action.onUpdateSearchQuery,
        modifier = Modifier.padding(horizontal = 16.dp),
      )

      Spacer(modifier = Modifier.height(16.dp))

      val searchResults = state.searchResultTags.take(MAX_SEARCH_RESULTS)
      val showCreateTag = state.searchQuery.isNotBlank()
        && state.searchResultTags.none { it.primaryName.equals(state.searchQuery.trim(), ignoreCase = true) }
      val showSearchResults = searchResults.isNotEmpty()
      val showSearchSection = showCreateTag || showSearchResults

      LazyColumn(modifier = Modifier.weight(1f)) {
        if (showSearchSection) {
          item(key = "search_header") {
            SectionHeader(text = stringResource(Res.string.manage_tags_search_results))
          }

          if (showCreateTag) {
            val trimmedQuery = state.searchQuery.trim()

            item(key = "create_tag") {
              HorizontalDivider()
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { action.onCreateTag(trimmedQuery) }
                  .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                  imageVector = Icons.Default.Numbers,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                  text = stringResource(Res.string.manage_tags_create, trimmedQuery),
                  style = MaterialTheme.typography.bodyLarge,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Medium,
                )
              }
            }
          }

          if (showSearchResults) {
            items(searchResults, key = { "search_${it.id.value}" }) { tag ->
              HorizontalDivider()
              TagRow(
                tag = tag,
                onDelete = { action.onDeleteTag(tag) },
              )
            }
          }

          item(key = "search_divider") {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
          }
        }

        item(key = "all_tags_header") {
          val allTags = state.tags
          AllTagsSectionHeader(
            totalCount = when (allTags) {
              is Loadable.Loaded -> allTags.value.size
              else -> null
            },
          )
        }

        when (val allTags = state.tags) {
          is Loadable.Loaded -> {
            items(allTags.value, key = { it.id.value }) { tag ->
              HorizontalDivider()
              TagRow(
                tag = tag,
                onDelete = { action.onDeleteTag(tag) },
              )
            }

            if (allTags.value.isNotEmpty()) {
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
    placeholder = { Text(stringResource(Res.string.manage_tags_search_placeholder)) },
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
private fun AllTagsSectionHeader(totalCount: Int?) {
  Row(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = stringResource(Res.string.manage_tags_section_all),
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.primary,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.weight(1f),
    )

    AnimatedVisibility(visible = totalCount != null) {
      if (totalCount != null) {
        Text(
          text = stringResource(Res.string.manage_tags_total, totalCount),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium,
        )
      }
    }
  }
}

@Composable
private fun TagRow(
  tag: Tag,
  onDelete: () -> Unit,
) {
  val haptic = rememberHapticFeedback()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Default.Numbers,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
      text = tag.primaryName,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.weight(1f),
    )
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

@Composable
private fun SectionHeader(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.Bold,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

private const val MAX_SEARCH_RESULTS = 10

@Preview
@Composable
private fun ManageTagScreenPreview() {
  AmenouzumeTheme {
    ManageTagScreen(
      state = ManageTagUiState(
        tags = Loadable.Loaded(
          listOf(
            Tag(TagId(1), "Cyberpunk"),
            Tag(TagId(2), "Noir"),
            Tag(TagId(3), "Photography"),
            Tag(TagId(4), "Architecture"),
            Tag(TagId(5), "UI/UX"),
          ),
        ),
        searchQuery = "",
      ),
      action = ManageTagUiAction.Noop,
    )
  }
}
