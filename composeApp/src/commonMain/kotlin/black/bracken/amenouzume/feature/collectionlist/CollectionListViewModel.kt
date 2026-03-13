package black.bracken.amenouzume.feature.collectionlist

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.uishared.navigation.AddCollectionRoute
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.moleculeState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.StateFlow

@Inject
@ViewModelKey(CollectionListViewModel::class)
@ContributesIntoMap(AppScope::class)
class CollectionListViewModel(
  private val navigator: Navigator,
) : ViewModel() {
  val uiState: StateFlow<CollectionListUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): CollectionListUiState = CollectionListUiState(
    collections = Loadable.Loaded(mockCollections),
    isBusy = false,
  )

  fun onBack() = navigator.back()

  fun onNavigateToAdd(vaultPath: String) = navigator.navigateSingleTop(AddCollectionRoute(vaultPath))
}

// TODO: remove
private val mockCollections = listOf(
  CollectionListEntry(id = "1", category = CollectionCategory.ILLUSTRATION, color = 0xFF7EC8C8),
  CollectionListEntry(id = "2", category = CollectionCategory.PHOTO, color = 0xFF5BA4A4),
  CollectionListEntry(id = "3", category = CollectionCategory.FANZINE, color = 0xFF2A8498),
  CollectionListEntry(id = "4", category = CollectionCategory.MOVIE, color = 0xFF1A6B7A),
  CollectionListEntry(id = "5", category = CollectionCategory.ILLUSTRATION, color = 0xFF9ED8D8),
  CollectionListEntry(id = "6", category = CollectionCategory.PHOTO, color = 0xFF4A9090),
  CollectionListEntry(id = "7", category = CollectionCategory.FANZINE, color = 0xFF3AACAC),
  CollectionListEntry(id = "8", category = CollectionCategory.MOVIE, color = 0xFF0D5260),
  CollectionListEntry(id = "9", category = CollectionCategory.ILLUSTRATION, color = 0xFF6BBEBE),
  CollectionListEntry(id = "10", category = CollectionCategory.PHOTO, color = 0xFF8DCFCF),
  CollectionListEntry(id = "11", category = CollectionCategory.FANZINE, color = 0xFF2E9090),
  CollectionListEntry(id = "12", category = CollectionCategory.MOVIE, color = 0xFF1C7070),
  CollectionListEntry(id = "13", category = CollectionCategory.ILLUSTRATION, color = 0xFFB0E0E0),
  CollectionListEntry(id = "14", category = CollectionCategory.PHOTO, color = 0xFF52AAAA),
  CollectionListEntry(id = "15", category = CollectionCategory.FANZINE, color = 0xFF40B8B8),
  CollectionListEntry(id = "16", category = CollectionCategory.MOVIE, color = 0xFF125E6E),
  CollectionListEntry(id = "17", category = CollectionCategory.ILLUSTRATION, color = 0xFF78C4C4),
  CollectionListEntry(id = "18", category = CollectionCategory.PHOTO, color = 0xFF60B0B0),
  CollectionListEntry(id = "19", category = CollectionCategory.FANZINE, color = 0xFF347E8A),
  CollectionListEntry(id = "20", category = CollectionCategory.MOVIE, color = 0xFF206878),
)
