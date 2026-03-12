package black.bracken.amenouzume.feature.collectionlist

sealed interface CollectionListUiState {
  data object Idle : CollectionListUiState

  data class Loaded(
    val collections: List<CollectionListEntry>,
  ) : CollectionListUiState
}

enum class CollectionCategory { ILLUSTRATION, PHOTO, FANZINE, MOVIE }

data class CollectionListEntry(
  val id: String,
  val category: CollectionCategory,
  val color: Long,
)
