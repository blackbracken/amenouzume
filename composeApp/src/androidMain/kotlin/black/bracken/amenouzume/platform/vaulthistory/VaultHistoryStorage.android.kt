package black.bracken.amenouzume.platform.vaulthistory

import android.content.Context
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class VaultHistoryStorage(
  private val context: Context,
) {
  private val file get() = File(context.filesDir, "vaults.txt")

  actual suspend fun loadPaths(): List<String> =
    withContext(Dispatchers.IO) {
      if (!file.exists()) return@withContext emptyList()
      file.readLines().filter { it.isNotBlank() }
    }

  actual suspend fun addPath(path: String) =
    withContext(Dispatchers.IO) {
      val existing = if (file.exists()) file.readLines().filter { it.isNotBlank() } else emptyList()
      if (path !in existing) {
        file.appendText("$path\n")
      }
    }

  actual suspend fun removePath(path: String) =
    withContext(Dispatchers.IO) {
      if (!file.exists()) return@withContext
      val remaining = file.readLines().filter { it.isNotBlank() && it != path }
      file.writeText(remaining.joinToString("\n", postfix = "\n"))
    }
}
