package black.bracken.amenouzume

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import black.bracken.amenouzume.uishared.navigation.AppNavHost
import black.bracken.amenouzume.uishared.navigation.OpenDatabaseRoute
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
  KoinContext {
    AmenouzumeTheme {
      val backStack = remember { mutableStateListOf<Any>(OpenDatabaseRoute) }
      AppNavHost(backStack = backStack)
    }
  }
}
