package black.bracken.amenouzume.kernel.repository

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_tag_already_exists
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.util.Loadable
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

@Inject
class TagRepository(
  private val database: AppDatabase,
) {
  private val tagQueries = database.tagQueries

  private val _allTags = MutableStateFlow<Loadable<List<Tag>>>(Loadable.Loading)
  private val _recentlyAddedTags = MutableStateFlow<Loadable<List<Tag>>>(Loadable.Loading)

  fun getAllTags(): Flow<Loadable<List<Tag>>> = _allTags.asStateFlow()

  fun getRecentlyAddedTags(): Flow<Loadable<List<Tag>>> = _recentlyAddedTags.asStateFlow()

  suspend fun refreshAllTags() {
    _allTags.value = Loadable.from {
      withContext(Dispatchers.IO) {
        tagQueries.selectAll().executeAsList().map { Tag.from(it) }
      }
    }
  }

  suspend fun refreshRecentlyAddedTags() {
    _recentlyAddedTags.value = Loadable.from {
      withContext(Dispatchers.IO) {
        tagQueries.selectRecentlyAdded().executeAsList().map { Tag.from(it) }
      }
    }
  }

  suspend fun createTag(name: String): Tag {
    return withContext(Dispatchers.IO) {
      val existing = tagQueries.selectByName(name).executeAsOneOrNull()
      if (existing != null) throw CommonFailure(Res.string.error_tag_already_exists)

      database.transactionWithResult {
        tagQueries.insert(name, Clock.System.now().toString())
        val id = TagId(tagQueries.lastInsertRowId().executeAsOne())
        Tag(id = id, primaryName = name)
      }.also {
        refreshAllTags()
        refreshRecentlyAddedTags()
      }
    }
  }
}
