package black.bracken.amenouzume.kernel.repository

import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.model.CollectionFileType
import black.bracken.amenouzume.kernel.model.CollectionId
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.util.TimeProvider
import black.bracken.amenouzume.util.runCatchingSafely
import dev.zacsweers.metro.Inject
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Inject
class CollectionRepository(
  private val database: AppDatabase,
  private val driverFactory: DatabaseDriverFactory,
) {
  private val queries = database.collectionQueries
  private val fileQueries = database.collectionFileQueries

  private data class FileEntry(
    val relativePath: String,
    val fileType: CollectionFileType,
    val displayOrder: Int,
  )

  suspend fun createCollection(
    title: String,
    category: String,
    filePaths: List<String>,
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
        val fileEntries = filePaths.mapIndexed { index, sourcePath ->
          val sourceFile = File(sourcePath)
          val ext = sourceFile.extension
          val uuid = UUID.randomUUID().toString()
          val destFile = File(collectionDir, "$uuid.$ext")

          sourceFile.copyTo(destFile)

          FileEntry(
            relativePath = "collection/${collectionId.value}/$uuid.$ext"
              .replace('\\', '/'),
            fileType = CollectionFileType.fromExtension(ext),
            displayOrder = index,
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
