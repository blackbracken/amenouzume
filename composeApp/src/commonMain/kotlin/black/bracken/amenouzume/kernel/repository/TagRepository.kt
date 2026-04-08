package black.bracken.amenouzume.kernel.repository

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_tag_already_exists
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagAlias
import black.bracken.amenouzume.kernel.model.TagAliasId
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.TimeProvider
import black.bracken.amenouzume.util.from
import black.bracken.amenouzume.util.runCatchingSafely
import black.bracken.amenouzume.util.toLoadable
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.fresh

@Inject
class TagRepository(
  private val database: AppDatabase,
  private val scope: CoroutineScope,
) {
  private val tagQueries = database.tagQueries
  private val tagAliasQueries = database.tagAliasQueries

  private val allTagsStore = StoreBuilder.from(
    fetcher = { _: Unit ->
      tagQueries.selectAll().executeAsList()
        .map { Tag.from(it) }
    },
    reader = { _: Unit ->
      tagQueries.selectAll().asFlow()
        .mapToList(Dispatchers.IO)
        .map { rows -> rows.map { Tag.from(it) } }
    },
  ).scope(scope).build()

  private val aliasesStore = StoreBuilder.from(
    fetcher = { tagId: TagId ->
      tagAliasQueries.selectByTagId(tagId.value).executeAsList()
        .map { TagAlias.from(it) }
    },
    reader = { tagId: TagId ->
      tagAliasQueries.selectByTagId(tagId.value).asFlow()
        .mapToList(Dispatchers.IO)
        .map { rows -> rows.map { TagAlias.from(it) } }
    },
  ).scope(scope).build()

  fun getAllTags(): Flow<Loadable<List<Tag>>> =
    allTagsStore.stream(StoreReadRequest.cached(Unit, refresh = false))
      .toLoadable()
      .onStart { emit(Loadable.Loading) }

  suspend fun searchTags(query: String, limit: Int): Result<List<Tag>> = runCatchingSafely {
    if (query.isBlank()) {
      emptyList()
    } else {
      withContext(Dispatchers.IO) {
        tagQueries.searchByName(query.trim(), limit.toLong()).executeAsList().map { Tag.from(it) }
      }
    }
  }

  suspend fun deleteTag(tag: Tag): Result<Unit> = runCatchingSafely {
    withContext(Dispatchers.IO) {
      tagQueries.deleteById(tag.id.value)
    }
    aliasesStore.clear(tag.id)
  }

  fun getAliases(tagId: TagId): Flow<Loadable<List<TagAlias>>> =
    aliasesStore.stream(StoreReadRequest.cached(tagId, refresh = false))
      .toLoadable()
      .onStart { emit(Loadable.Loading) }

  suspend fun getAliasesOnce(tagId: TagId): Result<List<TagAlias>> = runCatchingSafely {
    aliasesStore.fresh(tagId)
  }

  suspend fun updatePrimaryName(tagId: TagId, name: String): Result<Unit> = runCatchingSafely {
    val now = TimeProvider.now().toString()

    withContext(Dispatchers.IO) {
      tagQueries.updatePrimaryName(primary_name = name, updated_at = now, tag_id = tagId.value)
    }
  }

  suspend fun addAliases(tagId: TagId, names: Set<String>): Result<Unit> = runCatchingSafely {
    val now = TimeProvider.now().toString()

    withContext(Dispatchers.IO) {
      database.transaction {
        names.forEach { name ->
          val existingTag = tagQueries.selectByName(name).executeAsOneOrNull()
          if (existingTag != null) throw CommonFailure(Res.string.error_tag_already_exists)

          tagAliasQueries.insert(tag_id = tagId.value, name = name)
        }
        tagQueries.touchUpdatedAt(updated_at = now, tag_id = tagId.value)
      }
    }
  }

  suspend fun removeAliases(tagId: TagId, aliasIds: Set<TagAliasId>): Result<Unit> = runCatchingSafely {
    if (aliasIds.isNotEmpty()) {
      val now = TimeProvider.now().toString()

      withContext(Dispatchers.IO) {
        database.transaction {
          tagAliasQueries.deleteByIds(aliasIds.map { it.value })
          tagQueries.touchUpdatedAt(updated_at = now, tag_id = tagId.value)
        }
      }
    }
  }

  suspend fun createTag(name: String): Result<Tag> = runCatchingSafely {
    withContext(Dispatchers.IO) {
      val existing = tagQueries.selectByName(name).executeAsOneOrNull()
      if (existing != null) throw CommonFailure(Res.string.error_tag_already_exists)

      val existingAlias = tagAliasQueries.selectByName(name).executeAsOneOrNull()
      if (existingAlias != null) throw CommonFailure(Res.string.error_tag_already_exists)

      val now = TimeProvider.now()
      database.transactionWithResult {
        tagQueries.insert(name, now.toString(), now.toString())
        val id = TagId(tagQueries.lastInsertRowId().executeAsOne())
        Tag(id = id, primaryName = name, updatedAt = now)
      }
    }
  }
}
