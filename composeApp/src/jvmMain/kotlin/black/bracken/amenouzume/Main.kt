package black.bracken.amenouzume

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import black.bracken.amenouzume.db.DatabaseDriverFactory
import black.bracken.amenouzume.di.initKoin

fun main() {
  initKoin(DatabaseDriverFactory())
  application {
    Window(
      onCloseRequest = ::exitApplication,
      title = "amenouzume",
    ) {
      App()
    }
  }
}
