package black.bracken.amenouzume.uishared.navigation

import androidx.compose.runtime.Composable
import black.bracken.amenouzume.feature.addcollection.AddCollectionCoordinator
import black.bracken.amenouzume.feature.collectionlist.CollectionListCoordinator
import black.bracken.amenouzume.feature.opendatabase.OpenDatabaseCoordinator

@Composable
fun AppNavHost(backStack: List<AppRoute>) {
  when (val route = backStack.lastOrNull()) {
    is OpenDatabaseRoute -> OpenDatabaseCoordinator()
    is CollectionListRoute -> CollectionListCoordinator(vaultPath = route.vaultPath)
    is AddCollectionRoute -> AddCollectionCoordinator(vaultPath = route.vaultPath)
    null -> Unit
  }
}
