package black.bracken.amenouzume.feature.managetag

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import black.bracken.amenouzume.util.Loadable
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ManageTagScreenScreenshotTest {

  @Test
  fun manageTagScreen() = runDesktopComposeUiTest(
    width = 400,
    height = 800,
  ) {
    setContent {
      AmenouzumeTheme(darkTheme = false) {
        ManageTagScreen(
          state = ManageTagUiState(
            tags = Loadable.Loaded(
              listOf(
                Tag(TagId(1), "Cyberpunk"),
                Tag(TagId(2), "Noir"),
                Tag(TagId(3), "Photography"),
                Tag(TagId(4), "Architecture"),
                Tag(TagId(5), "UI/UX"),
                Tag(TagId(6), "Marketing"),
                Tag(TagId(7), "Design"),
              ),
            ),
            searchQuery = "",
          ),
          action = ManageTagUiAction.Noop,
        )
      }
    }
    onRoot().captureRoboImage("src/jvmTest/snapshots/ManageTagScreen.png")
  }

  @Test
  fun manageTagScreenSearching() = runDesktopComposeUiTest(
    width = 400,
    height = 800,
  ) {
    val allTags = listOf(
      Tag(TagId(1), "Cyberpunk"),
      Tag(TagId(2), "Noir"),
      Tag(TagId(3), "Photography"),
      Tag(TagId(4), "Architecture"),
      Tag(TagId(5), "UI/UX"),
      Tag(TagId(6), "Marketing"),
      Tag(TagId(7), "Design"),
    )

    setContent {
      AmenouzumeTheme(darkTheme = false) {
        ManageTagScreen(
          state = ManageTagUiState(
            tags = Loadable.Loaded(allTags),
            searchQuery = "Cyber",
            searchResultTags = listOf(Tag(TagId(1), "Cyberpunk")),
          ),
          action = ManageTagUiAction.Noop,
        )
      }
    }
    onRoot().captureRoboImage("src/jvmTest/snapshots/ManageTagScreenSearching.png")
  }
}
