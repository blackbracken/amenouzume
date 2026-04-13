package black.bracken.amenouzume.platform.vault

import java.io.File

actual class FileResolver {
  actual fun copyPickedFile(sourceLocation: String, destFile: File) {
    File(sourceLocation).copyTo(destFile)
  }

  actual fun getExtension(sourceLocation: String): String =
    File(sourceLocation).extension
}
