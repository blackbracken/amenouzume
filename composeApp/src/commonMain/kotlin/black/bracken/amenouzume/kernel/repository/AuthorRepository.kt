package black.bracken.amenouzume.kernel.repository

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_author_already_exists
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.kernel.model.Author
import black.bracken.amenouzume.kernel.model.AuthorAlias
import black.bracken.amenouzume.kernel.model.AuthorAliasId
import black.bracken.amenouzume.kernel.model.AuthorId
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.TimeProvider
import black.bracken.amenouzume.util.from
import black.bracken.amenouzume.util.fromSingleton
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
class AuthorRepository(
  private val database: AppDatabase,
  private val scope: CoroutineScope,
) {
  private val authorQueries = database.authorQueries
  private val authorAliasQueries = database.authorAliasQueries

  private val allAuthorsStore = StoreBuilder.fromSingleton(
    fetcher = {
      authorQueries.selectAll().executeAsList()
        .map { Author.from(it) }
    },
    reader = {
      authorQueries.selectAll().asFlow()
        .mapToList(Dispatchers.IO)
        .map { rows -> rows.map { Author.from(it) } }
    },
  ).scope(scope).build()

  private val aliasesStore = StoreBuilder.from(
    fetcher = { authorId: AuthorId ->
      authorAliasQueries.selectByAuthorId(authorId.value).executeAsList()
        .map { AuthorAlias.from(it) }
    },
    reader = { authorId: AuthorId ->
      authorAliasQueries.selectByAuthorId(authorId.value).asFlow()
        .mapToList(Dispatchers.IO)
        .map { rows -> rows.map { AuthorAlias.from(it) } }
    },
  ).scope(scope).build()

  fun getAllAuthors(): Flow<Loadable<List<Author>>> =
    allAuthorsStore.stream(StoreReadRequest.cached(Unit, refresh = false))
      .toLoadable()
      .onStart { emit(Loadable.Loading) }

  suspend fun searchAuthors(query: String, limit: Int): Result<List<Author>> = runCatchingSafely {
    if (query.isBlank()) {
      emptyList()
    } else {
      withContext(Dispatchers.IO) {
        authorQueries.searchByName(query.trim(), limit.toLong()).executeAsList().map { Author.from(it) }
      }
    }
  }

  suspend fun deleteAuthor(author: Author): Result<Unit> = runCatchingSafely {
    withContext(Dispatchers.IO) {
      authorQueries.deleteById(author.id.value)
    }
    aliasesStore.clear(author.id)
  }

  suspend fun getAliasesOnce(authorId: AuthorId): Result<List<AuthorAlias>> = runCatchingSafely {
    aliasesStore.fresh(authorId)
  }

  suspend fun updatePrimaryName(authorId: AuthorId, name: String): Result<Unit> = runCatchingSafely {
    val now = TimeProvider.now().toString()

    withContext(Dispatchers.IO) {
      val existingAlias = authorAliasQueries.selectByName(name).executeAsOneOrNull()
      if (existingAlias != null) throw CommonFailure(Res.string.error_author_already_exists)

      authorQueries.updatePrimaryName(primary_name = name, updated_at = now, author_id = authorId.value)
    }
  }

  suspend fun addAliases(authorId: AuthorId, names: Set<String>): Result<Unit> = runCatchingSafely {
    val now = TimeProvider.now().toString()

    withContext(Dispatchers.IO) {
      database.transaction {
        names.forEach { name ->
          val existingAuthor = authorQueries.selectByName(name).executeAsOneOrNull()
          if (existingAuthor != null) throw CommonFailure(Res.string.error_author_already_exists)

          authorAliasQueries.insert(author_id = authorId.value, name = name)
        }
        authorQueries.touchUpdatedAt(updated_at = now, author_id = authorId.value)
      }
    }
  }

  suspend fun removeAliases(authorId: AuthorId, aliasIds: Set<AuthorAliasId>): Result<Unit> = runCatchingSafely {
    if (aliasIds.isNotEmpty()) {
      val now = TimeProvider.now().toString()

      withContext(Dispatchers.IO) {
        database.transaction {
          authorAliasQueries.deleteByIds(aliasIds.map { it.value })
          authorQueries.touchUpdatedAt(updated_at = now, author_id = authorId.value)
        }
      }
    }
  }

  suspend fun createAuthor(name: String): Result<Author> = runCatchingSafely {
    withContext(Dispatchers.IO) {
      val existing = authorQueries.selectByName(name).executeAsOneOrNull()
      if (existing != null) throw CommonFailure(Res.string.error_author_already_exists)

      val existingAlias = authorAliasQueries.selectByName(name).executeAsOneOrNull()
      if (existingAlias != null) throw CommonFailure(Res.string.error_author_already_exists)

      val now = TimeProvider.now()
      database.transactionWithResult {
        authorQueries.insert(name, now.toString(), now.toString())
        val id = AuthorId(authorQueries.lastInsertRowId().executeAsOne())
        Author(id = id, primaryName = name, updatedAt = now)
      }
    }
  }
}
