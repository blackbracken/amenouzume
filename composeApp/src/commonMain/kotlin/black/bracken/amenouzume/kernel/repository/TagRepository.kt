package black.bracken.amenouzume.kernel.repository

import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.util.Loadable
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

@Inject
class TagRepository(
  private val database: AppDatabase,
) {
  private val tagQueries = database.tagQueries

  private val _allPrimaryNames = MutableStateFlow<Loadable<List<String>>>(Loadable.Loading)

  fun getAllPrimaryNames(): Flow<Loadable<List<String>>> = _allPrimaryNames.asStateFlow()

  suspend fun refreshAllPrimaryNames() {
    _allPrimaryNames.value = Loadable.from {
      withContext(Dispatchers.IO) {
        tagQueries.selectAll().executeAsList().map { it.primary_name }
      }
    }
  }

  suspend fun addTag(name: String): Long {
    return withContext(Dispatchers.IO) {
      val existing = tagQueries.selectByName(name).executeAsOneOrNull()
      if (existing != null) return@withContext existing.tag_id

      database.transactionWithResult {
        tagQueries.insert(name)
        tagQueries.lastInsertRowId().executeAsOne()
      }
    }
  }
}
