package black.bracken.amenouzume.feature.collectionlist

import black.bracken.amenouzume.uishared.ScreenUiState
import black.bracken.amenouzume.util.Loadable

data class CollectionListUiState(
  override val isBusy: Boolean,
  val collections: Loadable<List<CollectionListEntry>>,
) : ScreenUiState

enum class CollectionCategory { ILLUSTRATION, PHOTO, FANZINE, MOVIE }

data class CollectionListEntry(
  val id: String,
  val category: CollectionCategory,
  val color: Long,
)
