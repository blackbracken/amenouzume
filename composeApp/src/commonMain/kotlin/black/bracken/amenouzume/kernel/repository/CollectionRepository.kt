package black.bracken.amenouzume.kernel.repository

import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.model.CollectionId
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock

@Inject
class CollectionRepository(
  private val database: AppDatabase,
) {
  private val queries = database.collectionQueries

  suspend fun createCollection(
    title: String,
    category: String,
    contentType: String,
  ): CollectionId {
    return withContext(Dispatchers.IO) {
      database.transactionWithResult {
        val now = Clock.System.now().toString()
        queries.insert(
          title = title,
          thumbnail_path = null,
          category = category,
          created_at = now,
          updated_at = now,
          content_type = contentType,
        )
        CollectionId(queries.lastInsertRowId().executeAsOne())
      }
    }
  }
}
