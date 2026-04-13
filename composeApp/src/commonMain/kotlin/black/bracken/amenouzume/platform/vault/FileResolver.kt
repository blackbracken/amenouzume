package black.bracken.amenouzume.platform.vault

import java.io.File

expect class FileResolver {
  fun copyPickedFile(sourcePath: String, destFile: File)

  fun getExtension(sourcePath: String): String
}
