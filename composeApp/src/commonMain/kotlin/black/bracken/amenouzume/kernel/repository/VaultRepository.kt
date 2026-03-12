package black.bracken.amenouzume.kernel.repository

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_vault_already_exists
import amenouzume.composeapp.generated.resources.error_vault_not_readable
import amenouzume.composeapp.generated.resources.error_vault_not_sqlite
import black.bracken.amenouzume.kernel.error.CommonFailure
import black.bracken.amenouzume.kernel.model.VaultHistory
import black.bracken.amenouzume.platform.vault.VaultStorage
import black.bracken.amenouzume.platform.vaulthistory.VaultHistoryStorage
import black.bracken.amenouzume.util.runCatchingSafely
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private val SQLITE_MAGIC = "SQLite format 3\u0000".toByteArray(Charsets.UTF_8)

class VaultRepository(
  private val vaultStorage: VaultStorage,
  private val vaultHistoryStorage: VaultHistoryStorage,
) {
  suspend fun createVault(path: String): Result<Unit> = withContext(Dispatchers.IO) {
    runCatchingSafely {
      val targetFile = File(path, "amenouzume.db")
      if (targetFile.exists()) throw CommonFailure(Res.string.error_vault_already_exists)
      vaultStorage.createDatabaseFile(targetFile.absolutePath)
      vaultHistoryStorage.addPath(targetFile.absolutePath)
    }
  }

  suspend fun openVault(filePath: String): Result<Unit> = withContext(Dispatchers.IO) {
    runCatchingSafely {
      val file = File(filePath)
      if (!file.canRead()) throw CommonFailure(Res.string.error_vault_not_readable)
      val magic = ByteArray(16)
      file.inputStream().use { it.read(magic) }
      if (!magic.contentEquals(SQLITE_MAGIC)) throw CommonFailure(Res.string.error_vault_not_sqlite)
      vaultHistoryStorage.addPath(filePath)
    }
  }

  suspend fun loadVaultHistories(): Result<List<VaultHistory>> = withContext(Dispatchers.IO) {
    runCatchingSafely {
      vaultHistoryStorage.loadPaths()
        .filter { File(it).exists() }
        .map { File(it).toVaultHistory() }
    }
  }
}

private fun File.toVaultHistory() = VaultHistory(name = name, path = absolutePath, sizeBytes = length())
