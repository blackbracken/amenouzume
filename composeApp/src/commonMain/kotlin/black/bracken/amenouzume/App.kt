package black.bracken.amenouzume

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.uishared.navigation.AppNavHost
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import org.koin.compose.koinInject

@Composable
fun App() {
  AmenouzumeTheme {
    val navigator = koinInject<Navigator>()
    val backStack by navigator.backStack.collectAsStateWithLifecycle()
    AppNavHost(backStack = backStack)
  }
}
