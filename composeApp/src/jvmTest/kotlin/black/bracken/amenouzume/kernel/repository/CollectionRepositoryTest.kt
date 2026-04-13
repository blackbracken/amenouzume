package black.bracken.amenouzume.kernel.repository

import black.bracken.amenouzume.platform.vault.FileResolver
import black.bracken.amenouzume.rule.RepositoryTestRule
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class CollectionRepositoryTest {

  @get:Rule
  val rule = RepositoryTestRule()

  @Test
  fun `createCollection should コレクションIDが返される`() = runTest {
    val repository = CollectionRepository(rule.database, rule.driverFactory, FileResolver(), TestScope())

    val result = repository.createCollection(
      title = "test-collection",
      category = "ILLUSTRATION",
      filePaths = emptyList(),
    )

    assertTrue(result.isSuccess)
  }

  @Test
  fun `createCollection should 複数作成で異なるIDが返される`() = runTest {
    val repository = CollectionRepository(rule.database, rule.driverFactory, FileResolver(), TestScope())

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
    val sourceFile = File(rule.tempDir, "source.jpg").apply { writeText("image-data") }
    val repository = CollectionRepository(rule.database, rule.driverFactory, FileResolver(), TestScope())

    val id = repository.createCollection(
      title = "with-files",
      category = "ILLUSTRATION",
      filePaths = listOf(sourceFile.absolutePath),
    ).getOrThrow()

    val collectionDir = File(rule.tempDir, "collection/${id.value}")
    assertTrue(collectionDir.exists())

    val allFiles = collectionDir.listFiles()!!
    val contentFiles = allFiles.filter { it.name != "thumbnail.jpg" }
    assertEquals(1, contentFiles.size)
    assertTrue(contentFiles[0].name.endsWith(".jpg"))
    assertEquals("image-data", contentFiles[0].readText())

    val thumbnail = File(collectionDir, "thumbnail.jpg")
    assertTrue(thumbnail.exists())
    assertEquals("image-data", thumbnail.readText())
  }

  @Test
  fun `createCollection should 複数ファイルがdisplay_order順に保存される`() = runTest {
    val file1 = File(rule.tempDir, "a.png").apply { writeText("png-data") }
    val file2 = File(rule.tempDir, "b.mp4").apply { writeText("mp4-data") }
    val repository = CollectionRepository(rule.database, rule.driverFactory, FileResolver(), TestScope())

    val id = repository.createCollection(
      title = "multi-files",
      category = "MOVIE",
      filePaths = listOf(file1.absolutePath, file2.absolutePath),
    ).getOrThrow()

    val files = rule.database.collectionFileQueries.selectByCollectionId(id.value).executeAsList()
    assertEquals(2, files.size)
    assertEquals(0, files[0].display_order)
    assertEquals(1, files[1].display_order)
    assertEquals("IMAGE", files[0].file_type)
    assertEquals("VIDEO", files[1].file_type)
  }

  @Test
  fun `createCollection should 相対パスがスラッシュ区切りで保存される`() = runTest {
    val sourceFile = File(rule.tempDir, "test.pdf").apply { writeText("pdf-data") }
    val repository = CollectionRepository(rule.database, rule.driverFactory, FileResolver(), TestScope())

    val id = repository.createCollection(
      title = "pdf-collection",
      category = "FANZINE",
      filePaths = listOf(sourceFile.absolutePath),
    ).getOrThrow()

    val files = rule.database.collectionFileQueries.selectByCollectionId(id.value).executeAsList()
    assertEquals(1, files.size)
    assertTrue(files[0].file_path.startsWith("collection/${id.value}/"))
    assertTrue(files[0].file_path.endsWith(".pdf"))
    assertTrue('\\' !in files[0].file_path)
  }

  @Test
  fun `createCollection should 画像ファイルがあればthumbnail_pathが保存される`() = runTest {
    val sourceFile = File(rule.tempDir, "photo.png").apply { writeText("png-data") }
    val repository = CollectionRepository(rule.database, rule.driverFactory, FileResolver(), TestScope())

    val id = repository.createCollection(
      title = "with-thumbnail",
      category = "ILLUSTRATION",
      filePaths = listOf(sourceFile.absolutePath),
    ).getOrThrow()

    val collection = rule.database.collectionQueries.selectById(id.value).executeAsOne()
    assertEquals("collection/${id.value}/thumbnail.jpg", collection.thumbnail_path)
  }

  @Test
  fun `createCollection should 画像ファイルがなければthumbnail_pathはnull`() = runTest {
    val sourceFile = File(rule.tempDir, "video.mp4").apply { writeText("video-data") }
    val repository = CollectionRepository(rule.database, rule.driverFactory, FileResolver(), TestScope())

    val id = repository.createCollection(
      title = "video-only",
      category = "MOVIE",
      filePaths = listOf(sourceFile.absolutePath),
    ).getOrThrow()

    val collection = rule.database.collectionQueries.selectById(id.value).executeAsOne()
    assertNull(collection.thumbnail_path)
  }
}
