package black.bracken.amenouzume.kernel.repository

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_tag_already_exists
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagAlias
import black.bracken.amenouzume.kernel.model.TagAliasId
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.util.Loadable
import dev.zacsweers.metro.Inject
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

@Inject
class TagRepository(
  private val database: AppDatabase,
  private val scope: CoroutineScope,
) {
  private val tagQueries = database.tagQueries
  private val tagAliasQueries = database.tagAliasQueries

  private val _allTags = MutableStateFlow<Loadable<List<Tag>>>(Loadable.Loading)
  private val _recentlyAddedTags = MutableStateFlow<Loadable<List<Tag>>>(Loadable.Loading)
  private val _aliasesByTagId = mutableMapOf<TagId, MutableStateFlow<Loadable<List<TagAlias>>>>()
  private val aliasFlowCache = mutableMapOf<TagId, StateFlow<Loadable<List<TagAlias>>>>()

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
    _aliasesByTagId.remove(tag.id)
    aliasFlowCache.remove(tag.id)
    refreshAllTags()
    refreshRecentlyAddedTags()
  }

  fun getAliases(tagId: TagId): Flow<Loadable<List<TagAlias>>> = aliasFlowCache.getOrPut(tagId) {
    val backing = _aliasesByTagId.getOrPut(tagId) { MutableStateFlow(Loadable.Loading) }
    backing
      .onStart { refreshAliases(tagId) }
      .stateIn(scope, SharingStarted.Lazily, Loadable.Loading)
  }

  suspend fun getAliasesOnce(tagId: TagId): Loadable<List<TagAlias>> {
    _aliasesByTagId.getOrPut(tagId) { MutableStateFlow(Loadable.Loading) }
    refreshAliases(tagId)
    return _aliasesByTagId.getValue(tagId).value
  }

  private suspend fun refreshAliases(tagId: TagId) {
    val flow = _aliasesByTagId[tagId] ?: return
    flow.value = Loadable.from {
      withContext(Dispatchers.IO) {
        tagAliasQueries.selectByTagId(tagId.value).executeAsList().map { TagAlias.from(it) }
      }
    }
  }

  suspend fun updatePrimaryName(tagId: TagId, name: String) {
    val now = Clock.System.now().toString()

    withContext(Dispatchers.IO) {
      tagQueries.updatePrimaryName(primary_name = name, updated_at = now, tag_id = tagId.value)
    }

    refreshAllTags()
    refreshRecentlyAddedTags()
  }

  suspend fun addAliases(tagId: TagId, names: Set<String>) {
    val now = Clock.System.now().toString()

    withContext(Dispatchers.IO) {
      database.transaction {
        names.forEach { name ->
          tagAliasQueries.insert(tag_id = tagId.value, name = name)
        }
        tagQueries.touchUpdatedAt(updated_at = now, tag_id = tagId.value)
      }
    }

    refreshAliases(tagId)
    refreshAllTags()
    refreshRecentlyAddedTags()
  }

  suspend fun removeAliases(tagId: TagId, aliasIds: Set<TagAliasId>) {
    if (aliasIds.isEmpty()) return

    val now = Clock.System.now().toString()

    withContext(Dispatchers.IO) {
      database.transaction {
        tagAliasQueries.deleteByIds(aliasIds.map { it.value })
        tagQueries.touchUpdatedAt(updated_at = now, tag_id = tagId.value)
      }
    }

    refreshAliases(tagId)
    refreshAllTags()
    refreshRecentlyAddedTags()
  }

  suspend fun createTag(name: String): Tag = withContext(Dispatchers.IO) {
    val existing = tagQueries.selectByName(name).executeAsOneOrNull()
    if (existing != null) throw CommonFailure(Res.string.error_tag_already_exists)

    val existingAlias = tagAliasQueries.selectByName(name).executeAsOneOrNull()
    if (existingAlias != null) throw CommonFailure(Res.string.error_tag_already_exists)

    val now = Clock.System.now()
    database.transactionWithResult {
      tagQueries.insert(name, now.toString(), now.toString())
      val id = TagId(tagQueries.lastInsertRowId().executeAsOne())
      Tag(id = id, primaryName = name, updatedAt = now)
    }.also {
      refreshAllTags()
      refreshRecentlyAddedTags()
    }
  }
}
