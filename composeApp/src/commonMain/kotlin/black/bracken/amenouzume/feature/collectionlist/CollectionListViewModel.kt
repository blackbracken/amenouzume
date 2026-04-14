package black.bracken.amenouzume.feature.collectionlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.kernel.model.CollectionCategory
import black.bracken.amenouzume.kernel.model.CollectionId
import black.bracken.amenouzume.kernel.repository.CollectionRepository
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.uishared.navigation.AddCollectionRoute
import black.bracken.amenouzume.uishared.navigation.CollectionViewerRoute
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.map
import black.bracken.amenouzume.util.moleculeState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import java.io.File
import kotlinx.coroutines.flow.StateFlow

@Inject
@ViewModelKey(CollectionListViewModel::class)
@ContributesIntoMap(AppScope::class)
class CollectionListViewModel(
  private val collectionRepository: CollectionRepository,
  private val driverFactory: DatabaseDriverFactory,
  private val navigator: Navigator,
) : ViewModel() {
  val uiState: StateFlow<CollectionListUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): CollectionListUiState {
    val collectionsFlow = remember { collectionRepository.getAllCollections() }
    val collectionsLoadable by collectionsFlow.collectAsState(Loadable.Loading)

    val vaultRoot = remember {
      File(driverFactory.selectedPath).parentFile
    }

    val entries = collectionsLoadable.map { collections ->
      collections.map { collection ->
        CollectionListEntry(
          id = collection.id,
          title = collection.title,
          category = CollectionCategory.entries.find { cat -> cat.name == collection.category },
          thumbnailPath = collection.thumbnailPath?.let { path -> File(vaultRoot, path).absolutePath },
        )
      }
    }

    return CollectionListUiState(
      isBusy = false,
      collections = entries,
    )
  }

  fun onNavigateToAdd(vaultPath: String) = navigator.navigateSingleTop(AddCollectionRoute(vaultPath))

  fun onOpenCollection(collectionId: CollectionId) = navigator.navigate(CollectionViewerRoute(collectionId))
}
