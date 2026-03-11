package black.bracken.amenouzume.di

import black.bracken.amenouzume.feature.featureModule
import black.bracken.amenouzume.kernel.repository.VaultRepository
import black.bracken.amenouzume.platform.db.DatabaseDriverFactory
import black.bracken.amenouzume.platform.db.VaultStorage
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(
  driverFactory: DatabaseDriverFactory,
  vaultStorage: VaultStorage,
  config: KoinAppDeclaration? = null,
) {
  startKoin {
    config?.invoke(this)
    modules(
      databaseModule(driverFactory),
      module {
        single { vaultStorage }
        single { VaultRepository(get()) }
      },
      featureModule,
    )
  }
}
