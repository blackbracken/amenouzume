package black.bracken.amenouzume.kernel.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.model.Author
import black.bracken.amenouzume.kernel.model.Collection
import black.bracken.amenouzume.kernel.model.CollectionFile
import black.bracken.amenouzume.kernel.model.CollectionFileType
import black.bracken.amenouzume.kernel.model.CollectionId
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.platform.vault.FileResolver
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.TimeProvider
import black.bracken.amenouzume.util.from
import black.bracken.amenouzume.util.fromSingleton
import black.bracken.amenouzume.util.runCatchingSafely
import black.bracken.amenouzume.util.toLoadable
import dev.zacsweers.metro.Inject
import java.io.File
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreReadRequest

@Inject
class CollectionRepository(
  private val database: AppDatabase,
  private val driverFactory: DatabaseDriverFactory,
  private val fileResolver: FileResolver,
  private val scope: CoroutineScope,
) {
  private val queries = database.collectionQueries
  private val fileQueries = database.collectionFileQueries
  private val collectionTagQueries = database.collectionTagQueries
  private val collectionAuthorQueries = database.collectionAuthorQueries

  private val allCollectionsStore = StoreBuilder.fromSingleton(
    fetcher = {
      queries.selectAllOrderByUpdated().executeAsList()
        .map { Collection.from(it) }
    },
    reader = {
      queries.selectAllOrderByUpdated().asFlow()
        .mapToList(Dispatchers.IO)
        .map { rows -> rows.map { Collection.from(it) } }
    },
  ).scope(scope).build()

  suspend fun getCollectionById(id: CollectionId): Result<Collection> = runCatchingSafely {
    withContext(Dispatchers.IO) {
      val entity = queries.selectById(id.value).executeAsOne()
      Collection.from(entity)
    }
  }

  suspend fun getFilesByCollectionId(id: CollectionId): Result<List<CollectionFile>> = runCatchingSafely {
    withContext(Dispatchers.IO) {
      fileQueries.selectByCollectionId(id.value).executeAsList().map { entity ->
        CollectionFile(
          filePath = entity.file_path,
          fileType = CollectionFileType.valueOf(entity.file_type),
          displayOrder = entity.display_order.toInt(),
        )
      }
    }
  }

  suspend fun getTagsByCollectionId(id: CollectionId): Result<List<Tag>> = runCatchingSafely {
    withContext(Dispatchers.IO) {
      collectionTagQueries.selectTagsByCollectionId(id.value).executeAsList().map { Tag.from(it) }
    }
  }

  suspend fun getAuthorsByCollectionId(id: CollectionId): Result<List<Author>> = runCatchingSafely {
    withContext(Dispatchers.IO) {
      collectionAuthorQueries.selectAuthorsByCollectionId(id.value).executeAsList().map { Author.from(it) }
    }
  }

  fun getAllCollections(): Flow<Loadable<List<Collection>>> =
    allCollectionsStore.stream(StoreReadRequest.cached(Unit, refresh = false))
      .toLoadable()
      .onStart { emit(Loadable.Loading) }

  private data class FileEntry(
    val relativePath: String,
    val fileType: CollectionFileType,
    val displayOrder: Int,
  )

  suspend fun createCollection(
    title: String,
    category: String,
    filePaths: List<String>,
    tagIds: Set<Long> = emptySet(),
    authorIds: Set<Long> = emptySet(),
  ): Result<CollectionId> = runCatchingSafely {
    withContext(Dispatchers.IO) {
      val vaultRoot = File(driverFactory.selectedPath).parentFile

      val collectionId = database.transactionWithResult {
        val now = TimeProvider.now().toString()
        queries.insert(
          title = title,
          thumbnail_path = null,
          category = category,
          created_at = now,
          updated_at = now,
        )
        CollectionId(queries.lastInsertRowId().executeAsOne())
      }

      val collectionDir = File(vaultRoot, "collection/${collectionId.value}")
      collectionDir.mkdirs()

      try {
        val fileEntries = filePaths.mapIndexed { index, sourceLocation ->
          val ext = fileResolver.getExtension(sourceLocation)
          val uuid = UUID.randomUUID().toString()
          val destFile = File(collectionDir, "$uuid.$ext")

          fileResolver.copyPickedFile(sourceLocation, destFile)

          FileEntry(
            relativePath = "collection/${collectionId.value}/$uuid.$ext"
              .replace('\\', '/'),
            fileType = CollectionFileType.fromExtension(ext),
            displayOrder = index,
          )
        }

        val thumbnailEntry = fileEntries.firstOrNull { it.fileType == CollectionFileType.IMAGE }
        if (thumbnailEntry != null) {
          val thumbnailSrc = File(vaultRoot, thumbnailEntry.relativePath)
          val thumbnailDest = File(collectionDir, "thumbnail.jpg")
          thumbnailSrc.copyTo(thumbnailDest)

          val thumbnailRelativePath = "collection/${collectionId.value}/thumbnail.jpg"
          queries.updateThumbnailPath(
            thumbnail_path = thumbnailRelativePath,
            updated_at = TimeProvider.now().toString(),
            collection_id = collectionId.value,
          )
        }

        database.transaction {
          val now = TimeProvider.now().toString()
          fileEntries.forEach { entry ->
            fileQueries.insert(
              collection_id = collectionId.value,
              file_path = entry.relativePath,
              file_type = entry.fileType.name,
              display_order = entry.displayOrder.toLong(),
              created_at = now,
              updated_at = now,
            )
          }
          tagIds.forEach { tagId ->
            collectionTagQueries.insert(
              collection_id = collectionId.value,
              tag_id = tagId,
            )
          }
          authorIds.forEach { authorId ->
            collectionAuthorQueries.insert(
              collection_id = collectionId.value,
              author_id = authorId,
            )
          }
        }
      } catch (e: Exception) {
        collectionDir.deleteRecursively()
        database.transaction { queries.deleteById(collectionId.value) }
        throw e
      }

      collectionId
    }
  }
}
