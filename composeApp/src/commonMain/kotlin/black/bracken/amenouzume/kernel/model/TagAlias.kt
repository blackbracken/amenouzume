package black.bracken.amenouzume.kernel.model

import black.bracken.amenouzume.db.TagAlias as DbTagAlias

class TagAlias(
  val id: TagAliasId,
  val tagId: TagId,
  val name: String,
) {

  override fun equals(other: Any?): Boolean = other is TagAlias && id == other.id

  override fun hashCode(): Int = id.hashCode()

  override fun toString(): String = "TagAlias(id=$id, tagId=$tagId, name=$name)"

  companion object {
    fun from(entity: DbTagAlias) = TagAlias(
      id = TagAliasId(entity.tag_alias_id),
      tagId = TagId(entity.tag_id),
      name = entity.name,
    )
  }
}
