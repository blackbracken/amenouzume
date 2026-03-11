package black.bracken.amenouzume.platform

import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.repository.VaultRepository
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.platform.vault.VaultStorage
import black.bracken.amenouzume.platform.vaulthistory.VaultHistoryStorage
import org.koin.dsl.module

data class PlatformEnvironment(
  val driverFactory: DatabaseDriverFactory,
  val vaultStorage: VaultStorage,
  val vaultHistoryStorage: VaultHistoryStorage,
)

fun platformModule(env: PlatformEnvironment) = module {
  single { env.driverFactory }
  single { AppDatabase(driver = env.driverFactory.createDriver()) }
  single { env.vaultStorage }
  single { env.vaultHistoryStorage }
  single { VaultRepository(get(), get()) }
}
