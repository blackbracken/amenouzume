package black.bracken.amenouzume.feature.collectionlist

import black.bracken.amenouzume.kernel.model.CollectionCategory
import black.bracken.amenouzume.uishared.ScreenUiState
import black.bracken.amenouzume.util.Loadable

data class CollectionListUiState(
  override val isBusy: Boolean,
  val collections: Loadable<List<CollectionListEntry>>,
) : ScreenUiState

data class CollectionListEntry(
  val id: Long,
  val title: String,
  val category: CollectionCategory?,
  val thumbnailPath: String?,
)
