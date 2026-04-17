package black.bracken.amenouzume.e2e

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import black.bracken.amenouzume.App
import black.bracken.amenouzume.feature.collectionlist.CollectionListTestTags
import black.bracken.amenouzume.platform.launcher.LocalMultipleFilePickerLauncher
import black.bracken.amenouzume.rule.E2ERule
import black.bracken.amenouzume.uishared.navigation.CollectionListRoute
import kotlin.test.Test
import org.junit.Rule

@OptIn(ExperimentalTestApi::class)
class AppE2ETest {
  @get:Rule
  val rule = E2ERule()

  @Test
  fun `ManageTag should 作成したタグが一覧に表示される`() = runDesktopComposeUiTest(width = 400, height = 800) {
    rule.appGraph.navigator.navigateReplace(CollectionListRoute(rule.dbPath))

    setContent {
      CompositionLocalProvider(
        LocalMultipleFilePickerLauncher provides { _, onResult -> { onResult(listOf("/fake/image.jpg")) } },
      ) {
        App(rule.appGraph)
      }
    }

    waitUntil(timeoutMillis = 3_000) {
      onAllNodesWithTag(CollectionListTestTags.AddFab).fetchSemanticsNodes().isNotEmpty()
    }
    onNodeWithTag(CollectionListTestTags.AddFab).performClick()

    waitUntil(timeoutMillis = 3_000) {
      onAllNodesWithText("ILLUSTRATION").fetchSemanticsNodes().isNotEmpty()
    }
    onNodeWithText("ILLUSTRATION").performClick()

    waitUntil(timeoutMillis = 3_000) {
      onAllNodesWithText("Browse Files").fetchSemanticsNodes().isNotEmpty()
    }
    onNodeWithText("Browse Files").performClick()

    waitUntil(timeoutMillis = 3_000) {
      onAllNodesWithText("Tags").fetchSemanticsNodes().isNotEmpty()
    }
    onNodeWithText("Tags").performClick()

    waitUntil(timeoutMillis = 3_000) {
      onAllNodesWithText("Manage Tags").fetchSemanticsNodes().isNotEmpty()
    }
    onNodeWithText("Manage Tags").performClick()

    waitUntil(timeoutMillis = 3_000) {
      onAllNodesWithText("Search tags").fetchSemanticsNodes().isNotEmpty()
    }

    onNode(hasSetTextAction()).performTextInput("cyberpunk")
    mainClock.advanceTimeBy(1_000)

    waitUntil(timeoutMillis = 5_000) {
      onAllNodesWithText("Create tag cyberpunk").fetchSemanticsNodes().isNotEmpty()
    }
    onNodeWithText("Create tag cyberpunk").performClick()

    waitUntil(timeoutMillis = 3_000) {
      onAllNodesWithText("cyberpunk").fetchSemanticsNodes().isNotEmpty()
    }
    onNodeWithText("cyberpunk").assertIsDisplayed()
  }

  @Test
  fun `CollectionList should AddCollection画面が表示される`() = runDesktopComposeUiTest(width = 400, height = 800) {
    rule.appGraph.navigator.navigateReplace(CollectionListRoute(rule.dbPath))

    setContent {
      App(rule.appGraph)
    }

    waitUntil(timeoutMillis = 3_000) {
      onAllNodesWithTag(CollectionListTestTags.AddFab).fetchSemanticsNodes().isNotEmpty()
    }
    onNodeWithTag(CollectionListTestTags.AddFab).performClick()

    waitUntil(timeoutMillis = 3_000) {
      onAllNodesWithText("Add Collection").fetchSemanticsNodes().isNotEmpty()
    }
    onNodeWithText("Add Collection").assertIsDisplayed()
  }
}
