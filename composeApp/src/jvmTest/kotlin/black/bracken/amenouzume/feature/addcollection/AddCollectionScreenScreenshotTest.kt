package black.bracken.amenouzume.feature.addcollection

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AddCollectionScreenScreenshotTest {

  @Test
  fun addCollectionScreen() = runDesktopComposeUiTest(
    width = 400,
    height = 1200,
  ) {
    setContent {
      AmenouzumeTheme(darkTheme = false) {
        AddCollectionScreen(
          state = AddCollectionUiState(
            isBusy = false,
            selectedCategory = CollectionCategory.ILLUSTRATION,
            editing = AddCollectionUiState.Editing(
              title = "",
              authors = listOf("@jdoe_art"),
              tags = listOf("Cyberpunk", "Noir"),
              availableTags = listOf("Architecture", "Design", "Engineering"),
            ),
            errorMessage = null,
          ),
          action = AddCollectionUiAction.Noop,
        )
      }
    }
    onRoot().captureRoboImage("src/jvmTest/snapshots/AddCollectionScreen.png")
  }
}
