package black.bracken.amenouzume.platform.vault

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import black.bracken.amenouzume.db.AppDatabase

actual class DatabaseDriverFactory(
  private val context: Context,
) {
  actual var selectedPath: String? = null

  actual fun createDriver(): SqlDriver {
    val path = selectedPath ?: throw IllegalStateException("Database path is not selected")
    return AndroidSqliteDriver(AppDatabase.Schema, context, path)
  }
}
