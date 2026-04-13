package black.bracken.amenouzume

import android.app.Application
import black.bracken.amenouzume.di.AppGraph
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.platform.vault.FileResolver
import black.bracken.amenouzume.platform.vault.VaultStorage
import black.bracken.amenouzume.platform.vaulthistory.VaultHistoryStorage
import dev.zacsweers.metro.createGraphFactory

class AmenouzumeApplication : Application() {
  lateinit var graph: AppGraph
    private set

  override fun onCreate() {
    super.onCreate()

    graph = createGraphFactory<AppGraph.Factory>().create(
      driverFactory = DatabaseDriverFactory(applicationContext),
      fileResolver = FileResolver(applicationContext),
      vaultStorage = VaultStorage(applicationContext),
      vaultHistoryStorage = VaultHistoryStorage(applicationContext),
    )
  }
}
