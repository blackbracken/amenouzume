package black.bracken.amenouzume.uishared.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import black.bracken.amenouzume.feature.addcollection.AddCollectionCoordinator
import black.bracken.amenouzume.feature.collectionlist.CollectionListCoordinator
import black.bracken.amenouzume.feature.collectionviewer.CollectionViewerCoordinator
import black.bracken.amenouzume.feature.manageauthor.ManageAuthorCoordinator
import black.bracken.amenouzume.feature.managetag.ManageTagCoordinator
import black.bracken.amenouzume.feature.opendatabase.OpenDatabaseCoordinator

@Suppress("MoveLambdaOutsideParentheses")
private val noTransitionAnimations: List<(AppRoute?, AppRoute?) -> Boolean> = listOf(
  { from, to -> from is AddCollectionRoute && to is ManageTagRoute },
  { from, to -> from is AddCollectionRoute && to is ManageAuthorRoute },
)

@Composable
fun AppNavHost(backStack: List<AppRoute>) {
  val routeOwners = retain { mutableMapOf<AppRoute, RouteViewModelStoreOwner>() }

  backStack.forEach { route ->
    routeOwners.getOrPut(route) { RouteViewModelStoreOwner() }
  }

  var previousSize by remember { mutableIntStateOf(backStack.size) }
  val isForward = backStack.size >= previousSize

  LaunchedEffect(backStack) {
    previousSize = backStack.size
  }

  val currentBackStack = rememberUpdatedState(backStack)

  AnimatedContent(
    targetState = backStack.lastOrNull(),
    transitionSpec = {
      val noAnimation = noTransitionAnimations.any { matches -> matches(initialState, targetState) }

      when {
        noAnimation -> {
          EnterTransition.None togetherWith ExitTransition.None
        }

        isForward -> {
          slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        }

        else -> {
          slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
        }
      }
    },
  ) { route ->
    val owner = route?.let { routeOwners[it] }
    if (route != null && owner != null) {
      CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        DisposableEffect(route) {
          onDispose {
            if (route !in currentBackStack.value) {
              routeOwners.remove(route)?.clear()
            }
          }
        }
        when (route) {
          is OpenDatabaseRoute -> OpenDatabaseCoordinator()
          is CollectionListRoute -> CollectionListCoordinator(vaultPath = route.vaultPath)
          is AddCollectionRoute -> AddCollectionCoordinator(vaultPath = route.vaultPath)
          is ManageTagRoute -> ManageTagCoordinator()
          is ManageAuthorRoute -> ManageAuthorCoordinator()
          is CollectionViewerRoute -> CollectionViewerCoordinator(collectionId = route.collectionId)
        }
      }
    }
  }
}

private class RouteViewModelStoreOwner : ViewModelStoreOwner {
  override val viewModelStore = ViewModelStore()
  fun clear() = viewModelStore.clear()
}
