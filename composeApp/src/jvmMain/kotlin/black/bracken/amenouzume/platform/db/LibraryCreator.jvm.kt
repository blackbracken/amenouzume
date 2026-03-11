package black.bracken.amenouzume.platform.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.util.runCatchingSafely
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual class LibraryCreator {
  actual suspend fun create(path: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      runCatchingSafely {
        val targetFile = File(path, "amenouzume.db")
        check(!targetFile.exists()) { "選択したディレクトリには既にライブラリが存在します" }

        val driver = JdbcSqliteDriver("jdbc:sqlite:${targetFile.absolutePath}")
        AppDatabase.Schema.create(driver)
        driver.close()
      }
    }
}
