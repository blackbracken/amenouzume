package black.bracken.amenouzume.kernel.repository

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_tag_already_exists
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.util.Loadable
import dev.zacsweers.metro.Inject
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

@Inject
class TagRepository(
  private val database: AppDatabase,
  scope: CoroutineScope,
) {
  private val tagQueries = database.tagQueries

  private val _allTags = MutableStateFlow<Loadable<List<Tag>>>(Loadable.Loading)
  private val _recentlyAddedTags = MutableStateFlow<Loadable<List<Tag>>>(Loadable.Loading)

  val allTags: StateFlow<Loadable<List<Tag>>> = _allTags
    .onStart { refreshAllTags() }
    .stateIn(scope, SharingStarted.Lazily, Loadable.Loading)

  val recentlyAddedTags: StateFlow<Loadable<List<Tag>>> = _recentlyAddedTags
    .onStart { refreshRecentlyAddedTags() }
    .stateIn(scope, SharingStarted.Lazily, Loadable.Loading)

  private suspend fun refreshAllTags() {
    _allTags.value = Loadable.from {
      withContext(Dispatchers.IO) {
        tagQueries.selectAll().executeAsList().map { Tag.from(it) }
      }
    }
  }

  private suspend fun refreshRecentlyAddedTags() {
    _recentlyAddedTags.value = Loadable.from {
      withContext(Dispatchers.IO) {
        tagQueries.selectRecentlyAdded().executeAsList().map { Tag.from(it) }
      }
    }
  }

  suspend fun searchTags(query: String, limit: Int): List<Tag> {
    if (query.isBlank()) return emptyList()

    return withContext(Dispatchers.IO) {
      tagQueries.searchByName(query.trim(), limit.toLong()).executeAsList().map { Tag.from(it) }
    }
  }

  suspend fun deleteTag(tag: Tag) {
    withContext(Dispatchers.IO) {
      tagQueries.deleteById(tag.id.value)
    }
    refreshAllTags()
    refreshRecentlyAddedTags()
  }

  suspend fun createTag(name: String): Tag = withContext(Dispatchers.IO) {
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
