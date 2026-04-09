package black.bracken.amenouzume.kernel.model

import black.bracken.amenouzume.db.AuthorAlias as DbAuthorAlias

data class AuthorAlias(
  val id: AuthorAliasId,
  val authorId: AuthorId,
  val name: String,
) {

  companion object {
    fun from(entity: DbAuthorAlias) = AuthorAlias(
      id = AuthorAliasId(entity.author_alias_id),
      authorId = AuthorId(entity.author_id),
      name = entity.name,
    )
  }
}
