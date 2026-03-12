package black.bracken.amenouzume.uishared.navigation

import androidx.compose.runtime.Composable
import black.bracken.amenouzume.feature.collectionlist.CollectionListCoordinator
import black.bracken.amenouzume.feature.opendatabase.OpenDatabaseCoordinator

@Composable
fun AppNavHost(backStack: List<Any>, onNavigate: (Any) -> Unit, onBack: () -> Unit) {
  when (backStack.lastOrNull()) {
    is OpenDatabaseRoute -> OpenDatabaseCoordinator(
      onOpenVault = { path -> onNavigate(CollectionListRoute(vaultPath = path)) },
    )

    is CollectionListRoute -> CollectionListCoordinator(
      onBack = onBack,
    )
  }
}
