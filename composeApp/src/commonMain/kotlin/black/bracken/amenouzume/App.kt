package black.bracken.amenouzume

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.uishared.navigation.AppNavHost
import black.bracken.amenouzume.uishared.navigation.LocalNavigator
import black.bracken.amenouzume.uishared.navigation.Navigator
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

@Composable
fun App(metroViewModelFactory: MetroViewModelFactory, navigator: Navigator) {
  CompositionLocalProvider(
    LocalMetroViewModelFactory provides metroViewModelFactory,
    LocalNavigator provides navigator,
  ) {
    AmenouzumeTheme {
      val backStack by navigator.backStack.collectAsStateWithLifecycle()
      AppNavHost(backStack = backStack)
    }
  }
}
