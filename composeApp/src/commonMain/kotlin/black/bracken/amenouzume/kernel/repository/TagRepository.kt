package black.bracken.amenouzume.kernel.repository

import black.bracken.amenouzume.db.AppDatabase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Inject
class TagRepository(
  database: AppDatabase,
) {
  private val tagQueries = database.tagQueries
  private val tagNameQueries = database.tagNameQueries

  suspend fun getAllPrimaryNames(): List<String> =
    withContext(Dispatchers.IO) {
      tagNameQueries.selectAllPrimary().executeAsList().map { it.name }
    }

  suspend fun addTag(name: String): Long {
    return withContext(Dispatchers.IO) {
      val existing = tagNameQueries.selectByName(name).executeAsOneOrNull()
      if (existing != null) return@withContext existing.tag_id

      tagQueries.insert()
      val tagId = tagQueries.lastInsertRowId().executeAsOne()

      tagNameQueries.insert(
        tag_id = tagId,
        name = name,
        is_primary = 1,
      )

      tagId
    }
  }
}
