package black.bracken.amenouzume.feature.collectionviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.kernel.model.CollectionCategory
import black.bracken.amenouzume.kernel.model.CollectionId
import black.bracken.amenouzume.kernel.repository.CollectionRepository
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.launchWithCatching
import black.bracken.amenouzume.util.moleculeState
import black.bracken.amenouzume.util.runWithCatching
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import java.io.File
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource

@AssistedInject
class CollectionViewerViewModel(
  @Assisted val collectionId: CollectionId,
  private val collectionRepository: CollectionRepository,
  private val driverFactory: DatabaseDriverFactory,
  private val navigator: Navigator,
) : ViewModel() {
  private var content by mutableStateOf<Loadable<CollectionViewerUiState.Content>>(Loadable.Loading)
  private var errorMessage by mutableStateOf<StringResource?>(null)

  val uiState: StateFlow<CollectionViewerUiState> = moleculeState { presenter() }

  init {
    loadCollection()
  }

  @Composable
  private fun presenter(): CollectionViewerUiState = CollectionViewerUiState(
    content = content,
    errorMessage = errorMessage,
  )

  private fun loadCollection() = launchWithCatching(
    onFailure = {
      content = Loadable.Failed(it)
      errorMessage = it.messageRes
    },
  ) {
    val vaultRoot = File(driverFactory.selectedPath).parentFile

    val collectionDeferred = async { collectionRepository.getCollectionById(collectionId).getOrThrow() }
    val filesDeferred = async { collectionRepository.getFilesByCollectionId(collectionId).getOrThrow() }
    val tagsDeferred = async { collectionRepository.getTagsByCollectionId(collectionId).getOrThrow() }
    val authorsDeferred = async { collectionRepository.getAuthorsByCollectionId(collectionId).getOrThrow() }

    val collection = collectionDeferred.await()
    val files = filesDeferred.await()
    val tags = tagsDeferred.await()
    val authors = authorsDeferred.await()

    content = Loadable.Loaded(
      CollectionViewerUiState.Content(
        title = collection.title,
        category = CollectionCategory.valueOf(collection.category),
        primaryFilePath = files.firstOrNull()?.filePath?.let { File(vaultRoot, it).absolutePath },
        fileCount = files.size,
        tags = tags,
        authors = authors,
      ),
    )
  }

  fun onConsumeError() {
    errorMessage = null
  }

  fun onClose() = runWithCatching({ errorMessage = it.messageRes }) {
    navigator.back()
  }

  @AssistedFactory
  @ManualViewModelAssistedFactoryKey(Factory::class)
  @ContributesIntoMap(AppScope::class)
  fun interface Factory : ManualViewModelAssistedFactory {
    fun create(@Assisted collectionId: CollectionId): CollectionViewerViewModel
  }
}
