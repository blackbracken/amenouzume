package black.bracken.amenouzume.platform.vault

import java.io.File

expect class FileResolver {
  fun copyPickedFile(sourceLocation: String, destFile: File)

  fun getExtension(sourceLocation: String): String
}
