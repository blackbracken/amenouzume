package black.bracken.amenouzume.repository

import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.db.Collection
import kotlin.random.Random

class CollectionRepository(
  private val database: AppDatabase,
) {
  private val queries = database.collectionQueries

  fun getAllCollections(): List<Collection> = queries.selectAll().executeAsList()

  fun getCollectionById(id: String): Collection? = queries.selectById(id).executeAsOneOrNull()

  fun insertCollection(
    id: String,
    path: String,
  ) {
    queries.insert(id, path)
  }

  fun insertRandomCollection() {
    val randomId = generateRandomId()
    val randomPath = "/path/to/collection/$randomId"
    insertCollection(randomId, randomPath)
  }

  fun deleteCollection(id: String) {
    queries.deleteById(id)
  }

  private fun generateRandomId(): String = Random.nextInt(100000, 999999).toString()
}
