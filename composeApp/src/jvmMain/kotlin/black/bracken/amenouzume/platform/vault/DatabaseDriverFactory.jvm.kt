package black.bracken.amenouzume.platform.vault

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import black.bracken.amenouzume.db.AppDatabase
import java.io.File

actual class DatabaseDriverFactory {
  private var _selectedPath: String? = null
  actual var selectedPath: String
    get() = _selectedPath ?: throw IllegalStateException("Database path is not selected")
    set(value) {
      _selectedPath = value
    }

  actual fun createDriver(): SqlDriver {
    val path = selectedPath
    val databasePath = File(path)
    val databaseExists = databasePath.exists()
    return JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}").also {
      if (!databaseExists) AppDatabase.Schema.create(it)
      it.execute(null, "PRAGMA foreign_keys = ON;", 0)
    }
  }
}
