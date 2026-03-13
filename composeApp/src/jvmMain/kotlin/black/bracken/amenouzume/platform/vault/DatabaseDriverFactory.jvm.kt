package black.bracken.amenouzume.platform.vault

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import black.bracken.amenouzume.db.AppDatabase
import java.io.File

actual class DatabaseDriverFactory {
  actual var selectedPath: String? = null

  actual fun createDriver(): SqlDriver {
    val path = selectedPath ?: throw IllegalStateException("Database path is not selected")
    val databasePath = File(path)
    val databaseExists = databasePath.exists()
    return JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}").also {
      if (!databaseExists) AppDatabase.Schema.create(it)
    }
  }
}
