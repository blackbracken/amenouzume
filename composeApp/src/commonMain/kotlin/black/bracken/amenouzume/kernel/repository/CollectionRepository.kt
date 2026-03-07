package black.bracken.amenouzume.kernel.repository

import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.db.Collection

class CollectionRepository(
  private val database: AppDatabase,
) {
  private val queries = database.collectionQueries

  fun getAllCollections(): List<Collection> = queries.selectAll().executeAsList()

  fun getCollectionById(id: String): Collection? = queries.selectById(id).executeAsOneOrNull()

  fun deleteCollection(id: String) {
    queries.deleteById(id)
  }
}
