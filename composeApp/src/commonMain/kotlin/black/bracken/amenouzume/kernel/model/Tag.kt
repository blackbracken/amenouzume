package black.bracken.amenouzume.kernel.model

import black.bracken.amenouzume.db.Tag as DbTag

/**
 * Uniquely identified by [id]. Natural ordering is by [id] descending.
 */
class Tag(
  val id: TagId,
  val primaryName: String,
) : Comparable<Tag> {

  override fun compareTo(other: Tag): Int = other.id.value.compareTo(id.value)

  override fun equals(other: Any?): Boolean = other is Tag && id == other.id

  override fun hashCode(): Int = id.hashCode()

  override fun toString(): String = "Tag(id=$id, primaryName=$primaryName)"

  companion object {
    fun from(entity: DbTag) = Tag(
      id = TagId(entity.tag_id),
      primaryName = entity.primary_name,
    )
  }
}
