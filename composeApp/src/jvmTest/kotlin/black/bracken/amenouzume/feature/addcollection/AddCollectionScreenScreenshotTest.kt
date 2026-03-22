package black.bracken.amenouzume.feature.addcollection

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
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
              filePaths = listOf("/path/to/image1.png", "/path/to/image2.png", "/path/to/image3.png"),
              authors = listOf("@jdoe_art"),
              tags = listOf(Tag(TagId(1), "Cyberpunk", ""), Tag(TagId(2), "Noir", "")),
              tagSearchQuery = "",
              availableTags = listOf(Tag(TagId(3), "Architecture", ""), Tag(TagId(4), "Design", ""), Tag(TagId(5), "Engineering", "")),
              searchResultTags = emptyList(),
              recentTags = listOf(Tag(TagId(5), "Engineering", ""), Tag(TagId(4), "Design", "")),
            ),
            errorMessage = null,
            showTagsSheet = false,
          ),
          action = AddCollectionUiAction.Noop,
        )
      }
    }
    onRoot().captureRoboImage("src/jvmTest/snapshots/AddCollectionScreen.png")
  }
}
