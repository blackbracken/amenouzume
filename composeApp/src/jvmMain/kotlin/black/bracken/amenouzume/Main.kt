package black.bracken.amenouzume

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import black.bracken.amenouzume.db.DatabaseDriverFactory
import black.bracken.amenouzume.db.createDatabase
import black.bracken.amenouzume.repository.CollectionRepository

fun main() =
  application {
    val database = createDatabase(DatabaseDriverFactory())
    val repository = CollectionRepository(database)

    Window(
      onCloseRequest = ::exitApplication,
      title = "amenouzume",
    ) {
      App()
    }
  }
