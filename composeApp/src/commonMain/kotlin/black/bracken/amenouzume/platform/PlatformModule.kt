package black.bracken.amenouzume.platform

import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.platform.vault.VaultStorage
import black.bracken.amenouzume.platform.vaulthistory.VaultHistoryStorage

data class PlatformEnvironment(
  val driverFactory: DatabaseDriverFactory,
  val vaultStorage: VaultStorage,
  val vaultHistoryStorage: VaultHistoryStorage,
)
