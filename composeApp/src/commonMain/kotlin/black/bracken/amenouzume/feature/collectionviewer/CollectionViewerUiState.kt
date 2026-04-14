package black.bracken.amenouzume.feature.collectionviewer

import black.bracken.amenouzume.uishared.ScreenUiState

data class CollectionViewerUiState(
  override val isBusy: Boolean = false,
) : ScreenUiState
