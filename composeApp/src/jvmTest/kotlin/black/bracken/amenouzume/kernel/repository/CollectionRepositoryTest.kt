package black.bracken.amenouzume.kernel.repository

import black.bracken.amenouzume.kernel.repository.rule.RepositoryTestRule
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class CollectionRepositoryTest {

  @get:Rule
  val rule = RepositoryTestRule()

  @Test
  fun `createCollection should コレクションIDが返される`() = runTest {
    val repository = CollectionRepository(rule.database, rule.driverFactory)

    val result = repository.createCollection(
      title = "test-collection",
      category = "ILLUSTRATION",
      filePaths = emptyList(),
    )

    assertTrue(result.isSuccess)
  }

  @Test
  fun `createCollection should 複数作成で異なるIDが返される`() = runTest {
    val repository = CollectionRepository(rule.database, rule.driverFactory)

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
    val repository = CollectionRepository(rule.database, rule.driverFactory)

    val id = repository.createCollection(
      title = "with-files",
      category = "ILLUSTRATION",
      filePaths = listOf(sourceFile.absolutePath),
    ).getOrThrow()

    val collectionDir = File(rule.tempDir, "collection/${id.value}")
    assertTrue(collectionDir.exists())

    val copiedFiles = collectionDir.listFiles()!!
    assertEquals(1, copiedFiles.size)
    assertTrue(copiedFiles[0].name.endsWith(".jpg"))
    assertEquals("image-data", copiedFiles[0].readText())
  }

  @Test
  fun `createCollection should 複数ファイルがdisplay_order順に保存される`() = runTest {
    val file1 = File(rule.tempDir, "a.png").apply { writeText("png-data") }
    val file2 = File(rule.tempDir, "b.mp4").apply { writeText("mp4-data") }
    val repository = CollectionRepository(rule.database, rule.driverFactory)

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
    val repository = CollectionRepository(rule.database, rule.driverFactory)

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
}
