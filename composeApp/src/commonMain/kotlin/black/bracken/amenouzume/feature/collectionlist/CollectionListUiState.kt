package black.bracken.amenouzume.feature.collectionlist

import black.bracken.amenouzume.kernel.model.CollectionCategory
import black.bracken.amenouzume.kernel.model.CollectionId
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.uishared.ScreenUiState
import black.bracken.amenouzume.util.Loadable

data class CollectionListUiState(
  override val isBusy: Boolean,
  val collections: Loadable<List<CollectionListEntry>>,
  val filterTag: Tag?,
  val showAddFab: Boolean,
) : ScreenUiState

data class CollectionListEntry(
  val id: CollectionId,
  val title: String,
  val category: CollectionCategory?,
  val thumbnailPath: String?,
)
