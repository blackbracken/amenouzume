package black.bracken.amenouzume.di

import black.bracken.amenouzume.db.AppDatabase
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.platform.vault.VaultStorage
import black.bracken.amenouzume.platform.vaulthistory.VaultHistoryStorage
import black.bracken.amenouzume.uishared.navigation.Navigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(AppScope::class)
interface AppGraph : ViewModelGraph {
  val navigator: Navigator

  @Provides
  @SingleIn(AppScope::class)
  fun appDatabase(driverFactory: DatabaseDriverFactory): AppDatabase = AppDatabase(driver = driverFactory.createDriver())

  @DependencyGraph.Factory
  fun interface Factory {
    fun create(
      @Provides driverFactory: DatabaseDriverFactory,
      @Provides vaultStorage: VaultStorage,
      @Provides vaultHistoryStorage: VaultHistoryStorage,
    ): AppGraph
  }
}
