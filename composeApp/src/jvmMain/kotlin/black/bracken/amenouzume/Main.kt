package black.bracken.amenouzume

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import black.bracken.amenouzume.platform.PlatformEnvironment
import black.bracken.amenouzume.di.initKoin
import black.bracken.amenouzume.platform.db.DatabaseDriverFactory
import black.bracken.amenouzume.platform.db.VaultStorage

fun main() {
  initKoin(
    platformEnv = PlatformEnvironment(
      DatabaseDriverFactory(),
      VaultStorage(),
    ),
  )


  application {
    Window(
      onCloseRequest = ::exitApplication,
      title = "amenouzume",
    ) {
      App()
    }
  }
}
