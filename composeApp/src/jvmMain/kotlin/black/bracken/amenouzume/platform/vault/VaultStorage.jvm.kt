package black.bracken.amenouzume.platform.vault

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import black.bracken.amenouzume.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class VaultStorage {
  actual suspend fun createDatabaseFile(absolutePath: String) =
    withContext(Dispatchers.IO) {
      val driver = JdbcSqliteDriver("jdbc:sqlite:$absolutePath")
      AppDatabase.Schema.create(driver)
      driver.close()
    }
}
