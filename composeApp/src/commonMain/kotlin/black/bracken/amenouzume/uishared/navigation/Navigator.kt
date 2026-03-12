package black.bracken.amenouzume.uishared.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

val LocalNavigator = staticCompositionLocalOf<Navigator> {
  error("No Navigator provided")
}

@Inject
@SingleIn(AppScope::class)
class Navigator {
  private val _backStack = MutableStateFlow<List<AppRoute>>(listOf(OpenDatabaseRoute))
  val backStack: StateFlow<List<AppRoute>> = _backStack

  fun navigate(route: AppRoute) = _backStack.update { it + route }

  fun back() = _backStack.update { it.dropLast(1) }
}
