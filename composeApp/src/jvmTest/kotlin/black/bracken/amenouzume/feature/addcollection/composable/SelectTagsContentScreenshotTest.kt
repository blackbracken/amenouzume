package black.bracken.amenouzume.feature.addcollection.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SelectTagsContentScreenshotTest {
  @Test
  fun selectTagsContent() = runDesktopComposeUiTest(
    width = 400,
    height = 900,
  ) {
    setContent {
      AmenouzumeTheme(darkTheme = false) {
        Column {
          SelectTagsContent(
            selectedTags = listOf(Tag(TagId(1), "Design", ""), Tag(TagId(2), "UI/UX", ""), Tag(TagId(3), "Mobile", "")),
            searchQuery = "",
            onSearchQueryChange = {},
            searchResultTags = emptyList(),
            recentTags = listOf(Tag(TagId(4), "Photography", ""), Tag(TagId(2), "UI/UX", ""), Tag(TagId(5), "Marketing", "")),
            onRemoveTag = {},
            onCreateTag = {},
            onAttachTag = {},
            onNavigateToManageTags = {},
            onDone = {},
          )
        }
      }
    }
    onRoot().captureRoboImage("src/jvmTest/snapshots/SelectTagsContent.png")
  }
}
