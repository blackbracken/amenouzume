package black.bracken.amenouzume.feature.addcollection.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SelectTagsContentScreenshotTest {
  @Test
  fun selectTagsContent() =
    runDesktopComposeUiTest(
      width = 400,
      height = 900,
    ) {
      setContent {
        AmenouzumeTheme(darkTheme = false) {
          Column {
            SelectTagsContent(
              selectedTags = listOf("Design", "UI/UX", "Mobile"),
              availableTags = listOf("Architecture", "Design", "Engineering", "Marketing", "Photography", "UI/UX"),
              onToggleTag = {},
              onRemoveTag = {},
              onAddTag = {},
              onDone = {},
            )
          }
        }
      }
      onRoot().captureRoboImage("src/jvmTest/snapshots/SelectTagsContent.png")
    }
}
