package black.bracken.amenouzume.platform.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import black.bracken.amenouzume.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual class VaultStorage(
  private val context: Context,
) {
  actual suspend fun createDatabaseFile(absolutePath: String) =
    withContext(Dispatchers.IO) {
      context.deleteDatabase("amenouzume_cache.db")
      AndroidSqliteDriver(AppDatabase.Schema, context, "amenouzume_cache.db").use {
        // force DB file creation on disk
        AppDatabase(it).collectionQueries.selectAll().executeAsList()
      }
      context.getDatabasePath("amenouzume_cache.db").copyTo(File(absolutePath))
      Unit
    }
}
