package black.bracken.amenouzume.kernel.model

import black.bracken.amenouzume.db.Tag as DbTag

/**
 * Natural ordering is by [id] descending.
 */
data class Tag(
  val id: TagId,
  val primaryName: String,
  val updatedAt: String,
) : Comparable<Tag> {

  override fun compareTo(other: Tag): Int = other.id.value.compareTo(id.value)

  companion object {
    fun from(entity: DbTag) = Tag(
      id = TagId(entity.tag_id),
      primaryName = entity.primary_name,
      updatedAt = entity.updated_at,
    )
  }
}
