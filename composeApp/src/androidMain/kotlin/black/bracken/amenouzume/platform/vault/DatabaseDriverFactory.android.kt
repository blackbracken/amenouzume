package black.bracken.amenouzume.platform.vault

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import black.bracken.amenouzume.db.AppDatabase

actual class DatabaseDriverFactory(
  private val context: Context,
) {
  private var _selectedPath: String? = null
  actual var selectedPath: String
    get() = _selectedPath ?: throw IllegalStateException("Database path is not selected")
    set(value) { _selectedPath = value }

  actual fun createDriver(): SqlDriver {
    val path = selectedPath
    return AndroidSqliteDriver(
      schema = AppDatabase.Schema,
      context = context,
      name = path,
      callback = object : AndroidSqliteDriver.Callback(AppDatabase.Schema) {
        override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
          db.execSQL("PRAGMA foreign_keys = ON;")
        }
      },
    )
  }
}
