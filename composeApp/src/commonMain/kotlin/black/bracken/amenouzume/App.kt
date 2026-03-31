package black.bracken.amenouzume

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import black.bracken.amenouzume.di.AppGraph
import black.bracken.amenouzume.uishared.navigation.AppNavHost
import black.bracken.amenouzume.uishared.navigation.LocalNavigator
import black.bracken.amenouzume.uishared.theme.AmenouzumeTheme
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory

@Composable
fun App(appGraph: AppGraph) {
  CompositionLocalProvider(
    LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
    LocalNavigator provides appGraph.navigator,
  ) {
    AmenouzumeTheme {
      val backStack by appGraph.navigator.backStack.collectAsStateWithLifecycle()
      AppNavHost(backStack = backStack)
    }
  }
}
