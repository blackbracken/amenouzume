package black.bracken.amenouzume.kernel.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import black.bracken.amenouzume.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class CollectionRepositoryTest {

  @Test
  fun `createCollection should コレクションIDが返される`() = runTest {
    val repository = CollectionRepository(createTestDatabase())

    val result = repository.createCollection(
      title = "test-collection",
      category = "ILLUSTRATION",
    )

    assertTrue(result.isSuccess)
  }

  @Test
  fun `createCollection should 複数作成で異なるIDが返される`() = runTest {
    val repository = CollectionRepository(createTestDatabase())

    val id1 = repository.createCollection(
      title = "collection-1",
      category = "ILLUSTRATION",
    ).getOrThrow()
    val id2 = repository.createCollection(
      title = "collection-2",
      category = "PHOTO",
    ).getOrThrow()

    assertNotEquals(id1, id2)
  }

  private fun createTestDatabase(): AppDatabase {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
      AppDatabase.Schema.create(it)
      it.execute(null, "PRAGMA foreign_keys = ON;", 0)
    }
    return AppDatabase(driver)
  }
}
