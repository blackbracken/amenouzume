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
  database: AppDatabase,
) {
  private val tagQueries = database.tagQueries
  private val tagNameQueries = database.tagNameQueries

  private val _allPrimaryNames = MutableStateFlow<Loadable<List<String>>>(Loadable.Loading)

  fun getAllPrimaryNames(): Flow<Loadable<List<String>>> = _allPrimaryNames.asStateFlow()

  suspend fun refreshAllPrimaryNames() {
    _allPrimaryNames.value = Loadable.from {
      withContext(Dispatchers.IO) {
        tagNameQueries.selectAllPrimary().executeAsList().map { it.name }
      }
    }
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
