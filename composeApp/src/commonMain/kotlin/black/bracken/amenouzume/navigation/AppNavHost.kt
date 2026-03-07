package black.bracken.amenouzume.navigation

import androidx.compose.runtime.Composable
import black.bracken.amenouzume.ui.opendatabase.OpenDatabaseScreen

@Composable
fun AppNavHost(backStack: List<Any>) {
    when (backStack.lastOrNull()) {
        is OpenDatabaseRoute -> OpenDatabaseScreen()
    }
}
