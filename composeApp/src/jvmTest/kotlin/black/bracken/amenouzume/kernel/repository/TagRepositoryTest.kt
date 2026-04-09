package black.bracken.amenouzume.kernel.repository

import app.cash.turbine.test
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.kernel.repository.rule.RepositoryTestRule
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.TimeProvider
import kotlin.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class TagRepositoryTest {

  @get:Rule
  val rule = RepositoryTestRule()

  @Test
  fun `allTags should タグを返す`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    repository.createTag("tag-1")
    repository.createTag("tag-2")

    repository.getAllTags().test {
      skipItems(1)
      val item = awaitItem()
      assertTrue(item is Loadable.Loaded)
      assertEquals(2, item.value.size)
    }
  }

  @Test
  fun `allTags should updatedAtの降順、idの昇順 の順序の強さで返される`() = runTest {
    val t1 = Instant.parse("2025-01-01T00:00:00Z")
    val t2 = Instant.parse("2025-01-02T00:00:00Z")

    TimeProvider.override { t1 }
    val repository = TagRepository(rule.database, backgroundScope)
    val tag1 = repository.createTag("tag-1").getOrThrow()
    val tag2 = repository.createTag("tag-2").getOrThrow()
    repository.createTag("tag-3")

    TimeProvider.override { t2 }
    repository.updatePrimaryName(tag2.id, "tag-2-updated")

    repository.getAllTags().test {
      skipItems(1)
      val item = awaitItem()
      assertTrue(item is Loadable.Loaded)
      val names = item.value.map { it.primaryName }
      assertEquals(listOf("tag-2-updated", "tag-1", "tag-3"), names)
    }
  }

  @Test
  fun `createTag should 作成したタグが返される`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)

    val tag = repository.createTag("tag-1").getOrThrow()

    assertEquals("tag-1", tag.primaryName)
  }

  @Test
  fun `createTag should 重複名でCommonFailureが投げられる`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    repository.createTag("tag-1")

    assertFailsWith<CommonFailure> {
      repository.createTag("tag-1").getOrThrow()
    }
  }

  @Test
  fun `deleteTag should allTagsから除去される`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag = repository.createTag("tag-1").getOrThrow()

    repository.getAllTags().test {
      skipItems(1)
      val before = awaitItem()
      assertTrue(before is Loadable.Loaded)
      assertEquals(1, before.value.size)

      repository.deleteTag(tag)

      val after = awaitItem()
      assertTrue(after is Loadable.Loaded)
      assertTrue(after.value.isEmpty())
    }
  }

  @Test
  fun `searchTags should クエリに一致するタグを返す`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    repository.createTag("tag-1")
    repository.createTag("tag-2")
    repository.createTag("tag-1-sub")

    val results = repository.searchTags("tag-1", limit = 10).getOrThrow()
    assertEquals(2, results.size)
    assertTrue(results.all { "tag-1" in it.primaryName })

    val noMatch = repository.searchTags("tag-3", limit = 10).getOrThrow()
    assertTrue(noMatch.isEmpty())
  }

  @Test
  fun `searchTags should 空白クエリで空リストを返す`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    repository.createTag("tag-1")

    val results = repository.searchTags("  ", limit = 10).getOrThrow()

    assertTrue(results.isEmpty())
  }

  @Test
  fun `searchTags should primary nameとエイリアスの完全一致、前方一致、部分一致の優先順で返される`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    repository.createTag("pre-xyz")                                     // primary部分一致 (rank 4)
    repository.createTag("xyz")                                         // primary完全一致 (rank 0)
    repository.createTag("xyz-ext")                                     // primary前方一致 (rank 2)
    val tag2 = repository.createTag("tag-2").getOrThrow()
    repository.addAliases(tag2.id, setOf("xyz-alias"))                  // alias前方一致 (rank 3)
    val tag3 = repository.createTag("tag-3").getOrThrow()
    repository.addAliases(tag3.id, setOf("pre-xyz-alias"))              // alias部分一致 (rank 5)
    val tag4 = repository.createTag("tag-4").getOrThrow()
    repository.addAliases(tag4.id, setOf("has-xyz-inside"))             // alias部分一致 (rank 5)

    val results = repository.searchTags("xyz", limit = 10).getOrThrow()

    assertEquals(6, results.size)
    assertEquals("xyz", results[0].primaryName)         // primary完全一致
    assertEquals("xyz-ext", results[1].primaryName)     // primary前方一致
    assertEquals("tag-2", results[2].primaryName)       // alias前方一致
    assertEquals("pre-xyz", results[3].primaryName)     // primary部分一致
    assertEquals("tag-3", results[4].primaryName)       // alias部分一致
    assertEquals("tag-4", results[5].primaryName)       // alias部分一致
  }

  @Test
  fun `getAliases should エイリアスをFlowで返す`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag = repository.createTag("tag-1").getOrThrow()
    repository.addAliases(tag.id, setOf("tag-1-alias-1", "tag-1-alias-2"))

    repository.getAliases(tag.id).test {
      skipItems(1)
      val item = awaitItem()
      assertTrue(item is Loadable.Loaded)
      assertEquals(2, item.value.size)
    }
  }

  @Test
  fun `getAliases should addAliasesの後にFlowが更新される`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag = repository.createTag("tag-1").getOrThrow()

    repository.getAliases(tag.id).test {
      skipItems(1)
      val initial = awaitItem()
      assertTrue(initial is Loadable.Loaded)
      assertTrue(initial.value.isEmpty())

      repository.addAliases(tag.id, setOf("tag-1-alias"))

      val updated = awaitItem()
      assertTrue(updated is Loadable.Loaded)
      assertEquals(1, updated.value.size)
      assertEquals("tag-1-alias", updated.value[0].name)
    }
  }

  @Test
  fun `getAliases should removeAliasesの後にFlowが更新される`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag = repository.createTag("tag-1").getOrThrow()
    repository.addAliases(tag.id, setOf("tag-1-alias"))

    repository.getAliases(tag.id).test {
      skipItems(1)
      val initial = awaitItem()
      assertTrue(initial is Loadable.Loaded)
      assertEquals(1, initial.value.size)

      val aliasId = initial.value[0].id
      repository.removeAliases(tag.id, setOf(aliasId))

      val updated = awaitItem()
      assertTrue(updated is Loadable.Loaded)
      assertTrue(updated.value.isEmpty())
    }
  }

  @Test
  fun `getAliasesOnce should エイリアスを返す`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag = repository.createTag("tag-1").getOrThrow()
    repository.addAliases(tag.id, setOf("tag-1-alias-1", "tag-1-alias-2"))

    val result = repository.getAliasesOnce(tag.id)

    assertTrue(result.isSuccess)
    assertEquals(2, result.getOrThrow().size)
  }

  @Test
  fun `getAliasesOnce should 結果がgetAliasesのFlowにも反映される`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag = repository.createTag("tag-1").getOrThrow()
    repository.addAliases(tag.id, setOf("tag-1-alias"))

    repository.getAliasesOnce(tag.id)

    repository.getAliases(tag.id).test {
      skipItems(1)
      val item = awaitItem()
      assertTrue(item is Loadable.Loaded)
      assertEquals(1, item.value.size)
      assertEquals("tag-1-alias", item.value[0].name)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `updatePrimaryName should primary nameが更新される`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag = repository.createTag("tag-1").getOrThrow()

    repository.updatePrimaryName(tag.id, "tag-1-renamed")

    val results = repository.searchTags("tag-1-renamed", limit = 10).getOrThrow()
    assertEquals(1, results.size)
    assertEquals("tag-1-renamed", results[0].primaryName)
  }

  @Test
  fun `updatePrimaryName should allTagsが更新される`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag = repository.createTag("tag-1").getOrThrow()

    repository.getAllTags().test {
      skipItems(1)
      val before = awaitItem()
      assertTrue(before is Loadable.Loaded)
      assertEquals("tag-1", before.value[0].primaryName)

      repository.updatePrimaryName(tag.id, "tag-1-renamed")

      val after = awaitItem()
      assertTrue(after is Loadable.Loaded)
      assertEquals("tag-1-renamed", after.value[0].primaryName)
    }
  }

  @Test
  fun `updatePrimaryName should 既存エイリアスと同名には更新できない`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag1 = repository.createTag("tag-1").getOrThrow()
    val tag2 = repository.createTag("tag-2").getOrThrow()
    repository.addAliases(tag1.id, setOf("tag-1-alias"))

    val result = repository.updatePrimaryName(tag2.id, "tag-1-alias")
    assertTrue(result.isFailure)
  }

  @Test
  fun `addAliases should 他のタグのエイリアスと同名では追加できない`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag1 = repository.createTag("tag-1").getOrThrow()
    val tag2 = repository.createTag("tag-2").getOrThrow()
    repository.addAliases(tag1.id, setOf("tag-1-alias"))

    val result = repository.addAliases(tag2.id, setOf("tag-1-alias"))
    assertTrue(result.isFailure)
  }

  @Test
  fun `addAliases should 既存タグのprimary nameと同名のエイリアスは追加できない`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag1 = repository.createTag("tag-1").getOrThrow()
    val tag2 = repository.createTag("tag-2").getOrThrow()

    val result = repository.addAliases(tag2.id, setOf("tag-1"))
    assertTrue(result.isFailure)
  }

  @Test
  fun `createTag should 既存エイリアスと同名のprimary nameでは作成できない`() = runTest {
    val repository = TagRepository(rule.database, backgroundScope)
    val tag1 = repository.createTag("tag-1").getOrThrow()
    repository.addAliases(tag1.id, setOf("tag-1-alias"))

    assertFailsWith<CommonFailure> {
      repository.createTag("tag-1-alias").getOrThrow()
    }
  }
}
