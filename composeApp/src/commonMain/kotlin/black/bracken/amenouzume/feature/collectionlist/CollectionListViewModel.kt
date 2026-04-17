package black.bracken.amenouzume.feature.collectionlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.kernel.model.CollectionCategory
import black.bracken.amenouzume.kernel.model.CollectionId
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.kernel.repository.CollectionRepository
import black.bracken.amenouzume.kernel.repository.TagRepository
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.uishared.navigation.AddCollectionRoute
import black.bracken.amenouzume.uishared.navigation.CollectionViewerRoute
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.getOrNull
import black.bracken.amenouzume.util.map
import black.bracken.amenouzume.util.moleculeState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import java.io.File
import kotlinx.coroutines.flow.StateFlow

@AssistedInject
class CollectionListViewModel(
  @Assisted private val filterTagId: TagId?,
  @Assisted private val showAddFab: Boolean,
  private val collectionRepository: CollectionRepository,
  private val tagRepository: TagRepository,
  private val driverFactory: DatabaseDriverFactory,
  private val navigator: Navigator,
) : ViewModel() {
  val uiState: StateFlow<CollectionListUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): CollectionListUiState {
    val collectionsFlow = remember(filterTagId) {
      if (filterTagId != null) {
        collectionRepository.getCollectionsByTagId(filterTagId)
      } else {
        collectionRepository.getAllCollections()
      }
    }
    val collectionsLoadable by collectionsFlow.collectAsState(Loadable.Loading)

    val tagsFlow = remember { tagRepository.getAllTags() }
    val tagsLoadable by tagsFlow.collectAsState(Loadable.Loading)
    val filterTag = remember(filterTagId, tagsLoadable) {
      filterTagId?.let { id -> tagsLoadable.getOrNull().orEmpty().find { it.id == id } }
    }

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
      filterTag = filterTag,
      showAddFab = showAddFab,
    )
  }

  fun onNavigateToAdd(vaultPath: String) = navigator.navigateSingleTop(AddCollectionRoute(vaultPath))

  fun onOpenCollection(vaultPath: String, collectionId: CollectionId) =
    navigator.navigate(CollectionViewerRoute(collectionId = collectionId, vaultPath = vaultPath))

  @AssistedFactory
  @ManualViewModelAssistedFactoryKey(Factory::class)
  @ContributesIntoMap(AppScope::class)
  fun interface Factory : ManualViewModelAssistedFactory {
    fun create(
      @Assisted filterTagId: TagId?,
      @Assisted showAddFab: Boolean,
    ): CollectionListViewModel
  }
}
