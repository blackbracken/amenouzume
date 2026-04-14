package black.bracken.amenouzume.feature.collectionviewer

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import black.bracken.amenouzume.kernel.model.CollectionId
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.util.moleculeState
import black.bracken.amenouzume.util.runWithCatching
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.StateFlow

@AssistedInject
class CollectionViewerViewModel(
  @Assisted val collectionId: CollectionId,
  private val navigator: Navigator,
) : ViewModel() {
  val uiState: StateFlow<CollectionViewerUiState> = moleculeState { presenter() }

  @Composable
  private fun presenter(): CollectionViewerUiState = CollectionViewerUiState()

  fun onClose() = runWithCatching({}) {
    navigator.back()
  }

  @AssistedFactory
  @ManualViewModelAssistedFactoryKey(Factory::class)
  @ContributesIntoMap(AppScope::class)
  fun interface Factory : ManualViewModelAssistedFactory {
    fun create(@Assisted collectionId: CollectionId): CollectionViewerViewModel
  }
}
