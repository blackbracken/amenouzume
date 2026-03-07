package black.bracken.amenouzume.platform.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import black.bracken.amenouzume.db.AppDatabase
import java.io.File

actual class DatabaseDriverFactory {
  actual fun createDriver(): SqlDriver {
    val databasePath = File(System.getProperty("user.home"), ".amenouzume/amenouzume.db")
    val databaseExists = databasePath.exists()
    databasePath.parentFile?.mkdirs()
    return JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}").also {
      if (!databaseExists) AppDatabase.Schema.create(it)
    }
  }
}
