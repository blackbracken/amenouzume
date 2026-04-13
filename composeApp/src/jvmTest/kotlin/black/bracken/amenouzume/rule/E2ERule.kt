package black.bracken.amenouzume.rule

import black.bracken.amenouzume.di.AppGraph
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.platform.vault.FileResolver
import black.bracken.amenouzume.platform.vault.VaultStorage
import black.bracken.amenouzume.platform.vaulthistory.VaultHistoryStorage
import dev.zacsweers.metro.createGraphFactory
import java.io.File
import java.util.Locale
import kotlin.io.path.createTempDirectory
import org.junit.rules.ExternalResource

class E2ERule : ExternalResource() {
  lateinit var appGraph: AppGraph
  lateinit var dbPath: String

  private lateinit var savedLocale: Locale
  private lateinit var tempDir: File

  override fun before() {
    savedLocale = Locale.getDefault()
    Locale.setDefault(Locale.ENGLISH)

    tempDir = createTempDirectory("amenouzume-e2e").toFile()
    dbPath = File(tempDir, "test.db").absolutePath

    appGraph = createGraphFactory<AppGraph.Factory>().create(
      driverFactory = DatabaseDriverFactory().apply { selectedPath = dbPath },
      fileResolver = FileResolver(),
      vaultStorage = VaultStorage(),
      vaultHistoryStorage = VaultHistoryStorage(),
    )
  }

  override fun after() {
    Locale.setDefault(savedLocale)
    tempDir.deleteRecursively()
  }
}
