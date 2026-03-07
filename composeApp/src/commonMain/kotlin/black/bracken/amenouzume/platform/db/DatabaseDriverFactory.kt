package black.bracken.amenouzume.platform.db

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
  fun createDriver(): SqlDriver
}
