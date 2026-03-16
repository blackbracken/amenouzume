package black.bracken.amenouzume.kernel.model

import java.io.File

data class VaultHistory(
  val name: String,
  val path: String,
) {
  companion object {
    fun from(file: File) = VaultHistory(name = file.name, path = file.absolutePath)
  }
}
