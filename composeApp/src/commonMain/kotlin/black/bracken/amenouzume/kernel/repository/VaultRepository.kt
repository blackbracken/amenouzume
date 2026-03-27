package black.bracken.amenouzume.kernel.repository

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_vault_already_exists
import amenouzume.composeapp.generated.resources.error_vault_not_readable
import amenouzume.composeapp.generated.resources.error_vault_not_sqlite
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.kernel.model.VaultHistory
import black.bracken.amenouzume.platform.vault.DatabaseDriverFactory
import black.bracken.amenouzume.platform.vault.VaultStorage
import black.bracken.amenouzume.platform.vaulthistory.VaultHistoryStorage
import black.bracken.amenouzume.util.Loadable
import black.bracken.amenouzume.util.runCatchingSafely
import black.bracken.amenouzume.util.toLoadable
import dev.zacsweers.metro.Inject
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

private val SQLITE_MAGIC = "SQLite format 3\u0000".toByteArray(Charsets.UTF_8)

/**
 * Accessing vault data without calling [openVault] first will throw [IllegalStateException].
 */
@Inject
class VaultRepository(
  private val vaultStorage: VaultStorage,
  private val vaultHistoryStorage: VaultHistoryStorage,
  private val driverFactory: DatabaseDriverFactory,
  scope: CoroutineScope,
) {
  private val _vaultHistories = MutableStateFlow<Loadable<List<VaultHistory>>>(Loadable.Loading)

  val vaultHistories: StateFlow<Loadable<List<VaultHistory>>> = _vaultHistories
    .onStart { refreshVaultHistories() }
    .stateIn(scope, SharingStarted.Lazily, Loadable.Loading)

  suspend fun refreshVaultHistories() {
    _vaultHistories.value = runCatchingSafely {
      withContext(Dispatchers.IO) {
        vaultHistoryStorage
          .loadPaths()
          .filter { File(it).exists() }
          .map { VaultHistory.from(File(it)) }
      }
    }.toLoadable()
  }

  suspend fun createVault(path: String): Result<String> = runCatchingSafely {
    val vaultPath = withContext(Dispatchers.IO) {
      val targetFile = File(path, "amenouzume.db")
      if (targetFile.exists()) throw CommonFailure(Res.string.error_vault_already_exists)

      vaultStorage.createDatabaseFile(targetFile.absolutePath)
      vaultHistoryStorage.addPath(targetFile.absolutePath)
      targetFile.absolutePath
    }
    refreshVaultHistories()
    vaultPath
  }

  suspend fun removeVaultHistory(path: String): Result<Unit> = runCatchingSafely {
    withContext(Dispatchers.IO) {
      vaultHistoryStorage.removePath(path)
    }
    refreshVaultHistories()
  }

  suspend fun openVault(filePath: String): Result<Unit> = runCatchingSafely {
    withContext(Dispatchers.IO) {
      val file = File(filePath)
      if (!file.canRead()) throw CommonFailure(Res.string.error_vault_not_readable)

      val magic = ByteArray(16)
      file.inputStream().use { it.read(magic) }
      if (!magic.contentEquals(SQLITE_MAGIC)) throw CommonFailure(Res.string.error_vault_not_sqlite)

      vaultHistoryStorage.addPath(filePath)
      driverFactory.selectedPath = filePath
    }
    refreshVaultHistories()
  }
}
