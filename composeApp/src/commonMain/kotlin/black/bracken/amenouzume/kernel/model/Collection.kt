package black.bracken.amenouzume.kernel.model

import black.bracken.amenouzume.db.Collection as DbCollection
import kotlin.time.Instant

data class Collection(
  val id: CollectionId,
  val title: String,
  val category: String,
  val thumbnailPath: String?,
  val updatedAt: Instant,
) {
  companion object {
    fun from(entity: DbCollection) = Collection(
      id = CollectionId(entity.collection_id),
      title = entity.title,
      category = entity.category,
      thumbnailPath = entity.thumbnail_path,
      updatedAt = Instant.parse(entity.updated_at),
    )
  }
}
