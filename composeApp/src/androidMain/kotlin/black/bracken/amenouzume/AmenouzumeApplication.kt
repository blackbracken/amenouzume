package black.bracken.amenouzume

import android.app.Application
import black.bracken.amenouzume.platform.PlatformEnvironment
import black.bracken.amenouzume.di.initKoin
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.platform.vault.VaultStorage
import black.bracken.amenouzume.platform.vaulthistory.VaultHistoryStorage

class AmenouzumeApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    initKoin(
      platformEnv = PlatformEnvironment(
        DatabaseDriverFactory(applicationContext),
        VaultStorage(applicationContext),
        VaultHistoryStorage(applicationContext),
      ),
    )
  }
}
