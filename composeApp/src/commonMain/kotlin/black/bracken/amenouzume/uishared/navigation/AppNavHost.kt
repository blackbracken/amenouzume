package black.bracken.amenouzume.uishared.navigation

import androidx.compose.runtime.Composable
import black.bracken.amenouzume.feature.collectionlist.CollectionListCoordinator
import black.bracken.amenouzume.feature.opendatabase.OpenDatabaseCoordinator

@Composable
fun AppNavHost(backStack: List<AppRoute>) {
  when (backStack.lastOrNull()) {
    is OpenDatabaseRoute -> OpenDatabaseCoordinator()
    is CollectionListRoute -> CollectionListCoordinator()
    null -> Unit
  }
}
