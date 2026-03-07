package black.bracken.amenouzume.uishared.navigation

import androidx.compose.runtime.Composable
import black.bracken.amenouzume.feature.opendatabase.OpenDatabaseCoordinator

@Composable
fun AppNavHost(backStack: List<Any>) {
    when (backStack.lastOrNull()) {
        is OpenDatabaseRoute -> OpenDatabaseCoordinator()
    }
}
