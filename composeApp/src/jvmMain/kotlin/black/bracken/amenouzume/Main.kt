package black.bracken.amenouzume

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import black.bracken.amenouzume.di.AppGraph
import black.bracken.amenouzume.platform.PlatformEnvironment
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.platform.vault.VaultStorage
import black.bracken.amenouzume.platform.vaulthistory.VaultHistoryStorage
import dev.zacsweers.metro.createGraphFactory

fun main() {
  val graph = createGraphFactory<AppGraph.Factory>().create(
    PlatformEnvironment(
      DatabaseDriverFactory(),
      VaultStorage(),
      VaultHistoryStorage(),
    ),
  )

  application {
    Window(
      onCloseRequest = ::exitApplication,
      title = "amenouzume",
    ) {
      App(graph.metroViewModelFactory, graph.navigator)
    }
  }
}
