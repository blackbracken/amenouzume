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
    repository.createTag("tag1")
    repository.createTag("tag2")

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
    repository.createTag("a")
    repository.createTag("b")
    repository.createTag("c")
    repository.createTag("d")

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

    val tag = repository.createTag("test-tag")

    assertEquals("test-tag", tag.primaryName)
  }

  @Test
  fun `createTag should 重複名でCommonFailureが投げられる`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    repository.createTag("dup")

    assertFailsWith<CommonFailure> {
      repository.createTag("dup")
    }
  }

  @Test
  fun `deleteTag should allTagsから除去される`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    val tag = repository.createTag("to-delete")

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
    repository.createTag("kotlin")
    repository.createTag("java")
    repository.createTag("kotlin-coroutines")

    val results = repository.searchTags("kotlin", limit = 10)
    assertEquals(2, results.size)
    assertTrue(results.all { "kotlin" in it.primaryName })

    val noMatch = repository.searchTags("rust", limit = 10)
    assertTrue(noMatch.isEmpty())
  }

  @Test
  fun `searchTags should 空白クエリで空リストを返す`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    repository.createTag("tag")

    val results = repository.searchTags("  ", limit = 10)

    assertTrue(results.isEmpty())
  }

  @Test
  fun `searchTags should primary nameとエイリアスの完全一致、前方一致、部分一致の優先順で返される`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    repository.createTag("my-kotlin")
    repository.createTag("kotlin")
    repository.createTag("kotlin-coroutines")
    val tagA = repository.createTag("tag-a")
    repository.addAliases(tagA.id, setOf("kotlin-ext"))
    val tagB = repository.createTag("tag-b")
    repository.addAliases(tagB.id, setOf("has-kotlin"))
    val tagC = repository.createTag("tag-c")
    repository.addAliases(tagC.id, setOf("kotlin"))

    val results = repository.searchTags("kotlin", limit = 10)

    assertEquals(6, results.size)
    assertEquals("kotlin", results[0].primaryName)
    assertEquals("tag-c", results[1].primaryName)
    assertEquals("kotlin-coroutines", results[2].primaryName)
    assertEquals("tag-a", results[3].primaryName)
    assertEquals("my-kotlin", results[4].primaryName)
    assertEquals("tag-b", results[5].primaryName)
  }

  @Test
  fun `addAliases should 他のタグのエイリアスと同名では追加できない`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    val tag1 = repository.createTag("tag1")
    val tag2 = repository.createTag("tag2")
    repository.addAliases(tag1.id, setOf("shared-alias"))

    assertFails {
      repository.addAliases(tag2.id, setOf("shared-alias"))
    }
  }

  @Test
  fun `createTag should 既存エイリアスと同名のprimary nameでは作成できない`() = runTest {
    val repository = TagRepository(createTestDatabase(), backgroundScope)
    val tag = repository.createTag("tag")
    repository.addAliases(tag.id, setOf("alias-name"))

    assertFailsWith<CommonFailure> {
      repository.createTag("alias-name")
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
