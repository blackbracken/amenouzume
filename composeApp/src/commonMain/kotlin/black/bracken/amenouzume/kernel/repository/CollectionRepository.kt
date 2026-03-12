package black.bracken.amenouzume.kernel.repository

import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.db.Collection
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Inject
class CollectionRepository(
  private val database: AppDatabase,
) {
  private val queries = database.collectionQueries

  fun getAllCollections(): List<Collection> = queries.selectAll().executeAsList()

  fun getCollectionById(id: String): Collection? = queries.selectById(id).executeAsOneOrNull()

  fun deleteCollection(id: String) {
    queries.deleteById(id)
  }

  suspend fun addCollection(
    id: String,
    title: String,
    category: String,
    contentType: String,
  ) {
    withContext(Dispatchers.IO) {
      val now = System.currentTimeMillis()
      queries.insert(
        collection_id = id,
        title = title,
        thumbnail_path = null,
        category = category,
        created_at = now,
        updated_at = now,
        content_type = contentType,
      )
    }
  }
}
