package black.bracken.amenouzume.platform.vault

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import black.bracken.amenouzume.db.AppDatabase

actual class DatabaseDriverFactory(
  private val context: Context,
) {
  actual fun createDriver(): SqlDriver = AndroidSqliteDriver(AppDatabase.Schema, context, "amenouzume.db")
}
