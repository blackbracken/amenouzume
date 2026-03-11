package black.bracken.amenouzume.kernel.repository

import black.bracken.amenouzume.platform.db.VaultStorage
import black.bracken.amenouzume.util.runCatchingSafely
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class VaultRepository(
  private val vaultStorage: VaultStorage,
) {
  suspend fun createVault(path: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      runCatchingSafely {
        val targetFile = File(path, "amenouzume.db")
        check(!targetFile.exists()) { "選択したディレクトリには既にヴォールトが存在します" }
        vaultStorage.createDatabaseFile(targetFile.absolutePath)
      }
    }
}
