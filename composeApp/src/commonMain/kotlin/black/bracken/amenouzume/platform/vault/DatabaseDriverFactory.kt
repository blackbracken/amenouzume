package black.bracken.amenouzume.platform.vault

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
  var selectedPath: String?
  fun createDriver(): SqlDriver
}
