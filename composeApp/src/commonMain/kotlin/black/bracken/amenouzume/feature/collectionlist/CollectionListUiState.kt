package black.bracken.amenouzume.feature.collectionlist

import black.bracken.amenouzume.uishared.ScreenUiState
import black.bracken.amenouzume.util.Loadable

data class CollectionListUiState(
  override val isBusy: Boolean,
  val collections: Loadable<List<CollectionListEntry>>,
) : ScreenUiState

enum class CollectionCategory(
  val acceptableMimeTypes: List<String>,
) {
  ILLUSTRATION(listOf("image/*")),
  PHOTO(listOf("image/*")),
  FANZINE(listOf("image/*", "application/pdf")),
  MOVIE(listOf("video/*")),
}

data class CollectionListEntry(
  val id: String,
  val category: CollectionCategory,
  val color: Long,
)
