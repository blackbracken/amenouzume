package black.bracken.amenouzume.platform.vault

import java.io.File

actual class FileResolver {
  actual fun copyPickedFile(sourcePath: String, destFile: File) {
    File(sourcePath).copyTo(destFile)
  }

  actual fun getExtension(sourcePath: String): String =
    File(sourcePath).extension
}
