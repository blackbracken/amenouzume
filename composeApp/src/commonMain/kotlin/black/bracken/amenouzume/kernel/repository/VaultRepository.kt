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
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

private val SQLITE_MAGIC = "SQLite format 3\u0000".toByteArray(Charsets.UTF_8)

/**
 * Accessing vault data without calling [openVault] first will throw [IllegalStateException].
 */
@Inject
class VaultRepository(
  private val vaultStorage: VaultStorage,
  private val vaultHistoryStorage: VaultHistoryStorage,
  private val driverFactory: DatabaseDriverFactory,
) {
  private val _vaultHistories = MutableStateFlow<Loadable<List<VaultHistory>>>(Loadable.Loading)

  fun getVaultHistories(): Flow<Loadable<List<VaultHistory>>> = _vaultHistories.asStateFlow()

  suspend fun refreshVaultHistories() {
    _vaultHistories.value = Loadable.from {
      withContext(Dispatchers.IO) {
        vaultHistoryStorage
          .loadPaths()
          .filter { File(it).exists() }
          .map { VaultHistory.from(File(it)) }
      }
    }
  }

  suspend fun createVault(path: String): String {
    val vaultPath = withContext(Dispatchers.IO) {
      val targetFile = File(path, "amenouzume.db")
      if (targetFile.exists()) throw CommonFailure(Res.string.error_vault_already_exists)

      vaultStorage.createDatabaseFile(targetFile.absolutePath)
      vaultHistoryStorage.addPath(targetFile.absolutePath)
      targetFile.absolutePath
    }
    refreshVaultHistories()
    return vaultPath
  }

  suspend fun removeVaultHistory(path: String) {
    withContext(Dispatchers.IO) {
      vaultHistoryStorage.removePath(path)
    }
    refreshVaultHistories()
  }

  suspend fun openVault(filePath: String) {
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
