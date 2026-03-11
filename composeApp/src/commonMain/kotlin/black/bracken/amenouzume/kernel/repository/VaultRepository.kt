package black.bracken.amenouzume.kernel.repository

import black.bracken.amenouzume.platform.vault.VaultStorage
import black.bracken.amenouzume.platform.vaulthistory.VaultHistoryStorage
import black.bracken.amenouzume.util.runCatchingSafely
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class VaultRepository(
  private val vaultStorage: VaultStorage,
  private val vaultHistoryStorage: VaultHistoryStorage,
) {
  suspend fun createVault(path: String): Result<Unit> = withContext(Dispatchers.IO) {
    runCatchingSafely {
      val targetFile = File(path, "amenouzume.db")
      check(!targetFile.exists()) { "選択したディレクトリには既にヴォールトが存在します" }
      vaultStorage.createDatabaseFile(targetFile.absolutePath)
      vaultHistoryStorage.addPath(targetFile.absolutePath)
    }
  }

  suspend fun openVault(filePath: String): Result<Unit> = withContext(Dispatchers.IO) {
    runCatchingSafely {
      check(File(filePath).canRead()) { "ファイルを開けません" }
      vaultHistoryStorage.addPath(filePath)
    }
  }

  suspend fun loadVaultHistories(): Result<List<String>> = withContext(Dispatchers.IO) {
    runCatchingSafely {
      vaultHistoryStorage.loadPaths().filter { File(it).exists() }
    }
  }
}
