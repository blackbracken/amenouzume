package black.bracken.amenouzume.platform

import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.kernel.repository.VaultRepository
import black.bracken.amenouzume.platform.db.DatabaseDriverFactory
import black.bracken.amenouzume.platform.db.VaultStorage
import org.koin.dsl.module

data class PlatformEnvironment(
  val driverFactory: DatabaseDriverFactory,
  val vaultStorage: VaultStorage,
)

fun platformModule(env: PlatformEnvironment) = module {
  single { env.driverFactory }
  single { AppDatabase(driver = env.driverFactory.createDriver()) }
  single { env.vaultStorage }
  single { VaultRepository(get()) }
}
