package black.bracken.amenouzume.kernel.repository.rule

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.util.TimeProvider
import org.junit.rules.ExternalResource
import java.io.File
import kotlin.io.path.createTempDirectory

class RepositoryTestRule : ExternalResource() {
  lateinit var database: AppDatabase
    private set
  lateinit var driverFactory: DatabaseDriverFactory
    private set
  lateinit var tempDir: File
    private set

  override fun before() {
    tempDir = createTempDirectory("amenouzume-test").toFile()

    driverFactory = DatabaseDriverFactory().apply {
      selectedPath = File(tempDir, "amenouzume.db").absolutePath
    }

    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
      AppDatabase.Schema.create(it)
      it.execute(null, "PRAGMA foreign_keys = ON;", 0)
    }
    database = AppDatabase(driver)
  }

  override fun after() {
    TimeProvider.reset()
    tempDir.deleteRecursively()
  }
}
