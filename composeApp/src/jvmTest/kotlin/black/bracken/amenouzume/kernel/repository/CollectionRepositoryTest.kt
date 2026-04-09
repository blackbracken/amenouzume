package black.bracken.amenouzume.kernel.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class CollectionRepositoryTest {

  private val tempDir = createTempDirectory("amenouzume-test").toFile()

  @AfterTest
  fun tearDown() {
    tempDir.deleteRecursively()
  }

  @Test
  fun `createCollection should コレクションIDが返される`() = runTest {
    val repository = createRepository()

    val result = repository.createCollection(
      title = "test-collection",
      category = "ILLUSTRATION",
      filePaths = emptyList(),
    )

    assertTrue(result.isSuccess)
  }

  @Test
  fun `createCollection should 複数作成で異なるIDが返される`() = runTest {
    val repository = createRepository()

    val id1 = repository.createCollection(
      title = "collection-1",
      category = "ILLUSTRATION",
      filePaths = emptyList(),
    ).getOrThrow()
    val id2 = repository.createCollection(
      title = "collection-2",
      category = "PHOTO",
      filePaths = emptyList(),
    ).getOrThrow()

    assertNotEquals(id1, id2)
  }

  @Test
  fun `createCollection should ファイルがコピーされCollectionFileに保存される`() = runTest {
    val sourceFile = File(tempDir, "source.jpg").apply { writeText("image-data") }
    val repository = createRepository()

    val id = repository.createCollection(
      title = "with-files",
      category = "ILLUSTRATION",
      filePaths = listOf(sourceFile.absolutePath),
    ).getOrThrow()

    val collectionDir = File(tempDir, "collection/${id.value}")
    assertTrue(collectionDir.exists())

    val copiedFiles = collectionDir.listFiles()!!
    assertEquals(1, copiedFiles.size)
    assertTrue(copiedFiles[0].name.endsWith(".jpg"))
    assertEquals("image-data", copiedFiles[0].readText())
  }

  @Test
  fun `createCollection should 複数ファイルがdisplay_order順に保存される`() = runTest {
    val file1 = File(tempDir, "a.png").apply { writeText("png-data") }
    val file2 = File(tempDir, "b.mp4").apply { writeText("mp4-data") }
    val repository = createRepository()

    val id = repository.createCollection(
      title = "multi-files",
      category = "MOVIE",
      filePaths = listOf(file1.absolutePath, file2.absolutePath),
    ).getOrThrow()

    val db = createTestDatabase()
    val files = db.collectionFileQueries.selectByCollectionId(id.value).executeAsList()
    assertEquals(2, files.size)
    assertEquals(0, files[0].display_order)
    assertEquals(1, files[1].display_order)
    assertEquals("IMAGE", files[0].file_type)
    assertEquals("VIDEO", files[1].file_type)
  }

  @Test
  fun `createCollection should 相対パスがスラッシュ区切りで保存される`() = runTest {
    val sourceFile = File(tempDir, "test.pdf").apply { writeText("pdf-data") }
    val repository = createRepository()

    val id = repository.createCollection(
      title = "pdf-collection",
      category = "FANZINE",
      filePaths = listOf(sourceFile.absolutePath),
    ).getOrThrow()

    val db = createTestDatabase()
    val files = db.collectionFileQueries.selectByCollectionId(id.value).executeAsList()
    assertEquals(1, files.size)
    assertTrue(files[0].file_path.startsWith("collection/${id.value}/"))
    assertTrue(files[0].file_path.endsWith(".pdf"))
    assertTrue('\\' !in files[0].file_path)
  }

  private lateinit var _database: AppDatabase
  private lateinit var _driverFactory: DatabaseDriverFactory

  private fun createRepository(): CollectionRepository {
    _driverFactory = DatabaseDriverFactory().apply {
      selectedPath = File(tempDir, "amenouzume.db").absolutePath
    }
    _database = createTestDatabase()
    return CollectionRepository(_database, _driverFactory)
  }

  private fun createTestDatabase(): AppDatabase {
    if (::_database.isInitialized) return _database

    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
      AppDatabase.Schema.create(it)
      it.execute(null, "PRAGMA foreign_keys = ON;", 0)
    }
    _database = AppDatabase(driver)
    return _database
  }
}
