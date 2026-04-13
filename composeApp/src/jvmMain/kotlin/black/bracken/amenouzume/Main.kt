package black.bracken.amenouzume

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import black.bracken.amenouzume.di.AppGraph
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.platform.vault.FileResolver
import black.bracken.amenouzume.platform.vault.VaultStorage
import black.bracken.amenouzume.platform.vaulthistory.VaultHistoryStorage
import dev.zacsweers.metro.createGraphFactory

fun main() {
  val graph = createGraphFactory<AppGraph.Factory>().create(
    driverFactory = DatabaseDriverFactory(),
    fileResolver = FileResolver(),
    vaultStorage = VaultStorage(),
    vaultHistoryStorage = VaultHistoryStorage(),
  )

  application {
    Window(
      onCloseRequest = ::exitApplication,
      title = "amenouzume",
      state = rememberWindowState(size = DpSize(400.dp, 860.dp)),
    ) {
      App(graph)
    }
  }
}
