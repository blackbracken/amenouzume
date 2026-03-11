package black.bracken.amenouzume.platform.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.util.runCatchingSafely
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual class LibraryCreator(
  private val context: Context,
) {
  actual suspend fun create(path: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      runCatchingSafely {
        val targetFile = File(path, "amenouzume.db")
        check(!targetFile.exists()) { "選択したディレクトリには既にライブラリが存在します" }

        context.deleteDatabase("amenouzume_cache.db")
        AndroidSqliteDriver(AppDatabase.Schema, context, "amenouzume_cache.db").close()

        context.getDatabasePath("amenouzume_cache.db").copyTo(targetFile)
        Unit
      }
    }
}
