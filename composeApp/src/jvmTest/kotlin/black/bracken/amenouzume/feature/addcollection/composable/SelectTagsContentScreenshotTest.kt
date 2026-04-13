package black.bracken.amenouzume.feature.addcollection.composable

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.select_tags_create
import amenouzume.composeapp.generated.resources.select_tags_manage
import amenouzume.composeapp.generated.resources.select_tags_recommended
import amenouzume.composeapp.generated.resources.select_tags_search_placeholder
import amenouzume.composeapp.generated.resources.select_tags_title
import amenouzume.composeapp.generated.resources.select_tags_update
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import black.bracken.amenouzume.kernel.model.Tag
import black.bracken.amenouzume.kernel.model.TagId
import black.bracken.amenouzume.uishared.component.SelectItemsContent
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test
import kotlin.time.Instant

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
          SelectItemsContent(
            selectedItems = listOf(Tag(TagId(1), "Design", Instant.DISTANT_PAST), Tag(TagId(2), "UI/UX", Instant.DISTANT_PAST), Tag(TagId(3), "Mobile", Instant.DISTANT_PAST)),
            searchQuery = "",
            onSearchQueryChange = {},
            searchResultItems = emptyList(),
            recentItems = listOf(
              Tag(TagId(4), "Photography", Instant.DISTANT_PAST),
              Tag(TagId(2), "UI/UX", Instant.DISTANT_PAST),
              Tag(TagId(5), "Marketing", Instant.DISTANT_PAST),
            ),
            onRemoveItem = {},
            onCreateItem = {},
            onAttachItem = {},
            onNavigateToManage = {},
            onDone = {},
            itemName = { it.primaryName },
            itemKey = { it.id.value },
            icon = Icons.Default.Numbers,
            titleRes = Res.string.select_tags_title,
            searchPlaceholderRes = Res.string.select_tags_search_placeholder,
            createTextRes = Res.string.select_tags_create,
            manageTextRes = Res.string.select_tags_manage,
            updateTextRes = Res.string.select_tags_update,
            recommendedTextRes = Res.string.select_tags_recommended,
          )
        }
      }
    }
    onRoot().captureRoboImage("src/jvmTest/snapshots/SelectTagsContent.png")
  }
}
