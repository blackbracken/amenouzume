package black.bracken.amenouzume.kernel.repository

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_tag_already_exists
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.kernel.model.TagId
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

  suspend fun addTag(name: String): TagId {
    return withContext(Dispatchers.IO) {
      val existing = tagQueries.selectByName(name).executeAsOneOrNull()
      if (existing != null) throw CommonFailure(Res.string.error_tag_already_exists)

      database.transactionWithResult {
        tagQueries.insert(name)
        TagId(tagQueries.lastInsertRowId().executeAsOne())
      }.also { refreshAllPrimaryNames() }
    }
  }
}
