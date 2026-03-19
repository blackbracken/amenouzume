package black.bracken.amenouzume.kernel.model

import black.bracken.amenouzume.db.Tag as DbTag

data class Tag(
  val id: TagId,
  val primaryName: String,
) {
  companion object {
    fun from(entity: DbTag) = Tag(
      id = TagId(entity.tag_id),
      primaryName = entity.primary_name,
    )
  }
}
