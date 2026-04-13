package black.bracken.amenouzume.kernel.model

import black.bracken.amenouzume.db.Author as DbAuthor
import kotlin.time.Instant

/**
 * Natural ordering is by [id] descending.
 */
data class Author(
  val id: AuthorId,
  val primaryName: String,
  val updatedAt: Instant,
) : Comparable<Author> {

  override fun compareTo(other: Author): Int = other.id.value.compareTo(id.value)

  companion object {
    fun from(entity: DbAuthor) = Author(
      id = AuthorId(entity.author_id),
      primaryName = entity.primary_name,
      updatedAt = Instant.parse(entity.updated_at),
    )
  }
}
