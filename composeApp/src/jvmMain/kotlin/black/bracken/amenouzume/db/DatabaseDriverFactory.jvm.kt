package black.bracken.amenouzume.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
  actual fun createDriver(): SqlDriver {
    val databasePath = File(System.getProperty("user.home"), ".amenouzume/amenouzume.db")
    databasePath.parentFile?.mkdirs()

    val driver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")
    AppDatabase.Schema.create(driver)
    return driver
  }
}
