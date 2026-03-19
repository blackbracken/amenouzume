package black.bracken.amenouzume.uishared.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import black.bracken.amenouzume.feature.addcollection.AddCollectionCoordinator
import black.bracken.amenouzume.feature.collectionlist.CollectionListCoordinator
import black.bracken.amenouzume.feature.opendatabase.OpenDatabaseCoordinator

@Composable
fun AppNavHost(backStack: List<AppRoute>) {
  var previousSize by remember { mutableIntStateOf(backStack.size) }
  val isForward = backStack.size >= previousSize

  LaunchedEffect(backStack) {
    previousSize = backStack.size
  }

  AnimatedContent(
    targetState = backStack.lastOrNull(),
    transitionSpec = {
      if (isForward) {
        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
      } else {
        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
      }
    },
  ) { route ->
    when (route) {
      is OpenDatabaseRoute -> OpenDatabaseCoordinator()
      is CollectionListRoute -> CollectionListCoordinator(vaultPath = route.vaultPath)
      is AddCollectionRoute -> AddCollectionCoordinator(vaultPath = route.vaultPath)
      null -> Unit
    }
  }
}
