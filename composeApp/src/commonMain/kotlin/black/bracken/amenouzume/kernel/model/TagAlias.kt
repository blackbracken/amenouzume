package black.bracken.amenouzume.kernel.model

import black.bracken.amenouzume.db.TagAlias as DbTagAlias

data class TagAlias(
  val id: TagAliasId,
  val tagId: TagId,
  val name: String,
) {

  companion object {
    fun from(entity: DbTagAlias) = TagAlias(
      id = TagAliasId(entity.tag_alias_id),
      tagId = TagId(entity.tag_id),
      name = entity.name,
    )
  }
}
