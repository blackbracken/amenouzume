package black.bracken.amenouzume.kernel.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.util.Loadable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFails
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class TagRepositoryTest {

  @Test
  fun `allTags should タグを返す`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    repository.createTag("tag-1")
    repository.createTag("tag-2")

    repository.allTags.test {
      skipItems(1)
      val item = awaitItem()
      assertTrue(item is Loadable.Loaded)
      assertEquals(2, item.value.size)
    }
  }

  @Test
  fun `recentlyAddedTags should 4件以上なら最新3件のみ返す`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    repository.createTag("tag-1")
    repository.createTag("tag-2")
    repository.createTag("tag-3")
    repository.createTag("tag-4")

    repository.recentlyAddedTags.test {
      skipItems(1)
      val item = awaitItem()
      assertTrue(item is Loadable.Loaded)
      assertEquals(3, item.value.size)
    }
  }

  @Test
  fun `createTag should 作成したタグが返される`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)

    val tag = repository.createTag("tag-1")

    assertEquals("tag-1", tag.primaryName)
  }

  @Test
  fun `createTag should 重複名でCommonFailureが投げられる`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    repository.createTag("tag-1")

    assertFailsWith<CommonFailure> {
      repository.createTag("tag-1")
    }
  }

  @Test
  fun `deleteTag should allTagsから除去される`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    val tag = repository.createTag("tag-1")

    repository.allTags.test {
      skipItems(1)
      val before = awaitItem()
      assertTrue(before is Loadable.Loaded)
      assertEquals(1, before.value.size)

      repository.deleteTag(tag)

      val after = awaitItem()
      assertTrue(after is Loadable.Loaded)
      assertTrue(after.value.isEmpty())
    }

    repository.recentlyAddedTags.test {
      skipItems(1)
      val item = awaitItem()
      assertTrue(item is Loadable.Loaded)
      assertTrue(item.value.isEmpty())
    }
  }

  @Test
  fun `searchTags should クエリに一致するタグを返す`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    repository.createTag("tag-1")
    repository.createTag("tag-2")
    repository.createTag("tag-1-sub")

    val results = repository.searchTags("tag-1", limit = 10)
    assertEquals(2, results.size)
    assertTrue(results.all { "tag-1" in it.primaryName })

    val noMatch = repository.searchTags("tag-3", limit = 10)
    assertTrue(noMatch.isEmpty())
  }

  @Test
  fun `searchTags should 空白クエリで空リストを返す`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    repository.createTag("tag-1")

    val results = repository.searchTags("  ", limit = 10)

    assertTrue(results.isEmpty())
  }

  @Test
  fun `searchTags should primary nameとエイリアスの完全一致、前方一致、部分一致の優先順で返される`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    repository.createTag("pre-tag-1")
    repository.createTag("tag-1")
    repository.createTag("tag-1-ext")
    val tag2 = repository.createTag("tag-2")
    repository.addAliases(tag2.id, setOf("tag-1-alias"))
    val tag3 = repository.createTag("tag-3")
    repository.addAliases(tag3.id, setOf("pre-tag-1-alias"))
    val tag4 = repository.createTag("tag-4")
    repository.addAliases(tag4.id, setOf("tag-1"))

    val results = repository.searchTags("tag-1", limit = 10)

    assertEquals(6, results.size)
    assertEquals("tag-1", results[0].primaryName)
    assertEquals("tag-4", results[1].primaryName)
    assertEquals("tag-1-ext", results[2].primaryName)
    assertEquals("tag-2", results[3].primaryName)
    assertEquals("pre-tag-1", results[4].primaryName)
    assertEquals("tag-3", results[5].primaryName)
  }

  @Test
  fun `addAliases should 他のタグのエイリアスと同名では追加できない`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    val tag1 = repository.createTag("tag-1")
    val tag2 = repository.createTag("tag-2")
    repository.addAliases(tag1.id, setOf("tag-1-alias"))

    assertFails {
      repository.addAliases(tag2.id, setOf("tag-1-alias"))
    }
  }

  @Test
  fun `createTag should 既存エイリアスと同名のprimary nameでは作成できない`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    val tag1 = repository.createTag("tag-1")
    repository.addAliases(tag1.id, setOf("tag-1-alias"))

    assertFailsWith<CommonFailure> {
      repository.createTag("tag-1-alias")
    }
  }

  private fun createTestDatabase(): AppDatabase {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
      AppDatabase.Schema.create(it)
      it.execute(null, "PRAGMA foreign_keys = ON;", 0)
    }
    return AppDatabase(driver)
  }
}
