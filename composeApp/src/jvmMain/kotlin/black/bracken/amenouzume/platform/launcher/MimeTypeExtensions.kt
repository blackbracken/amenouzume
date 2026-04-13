package black.bracken.amenouzume.platform.launcher

import javax.swing.filechooser.FileNameExtensionFilter

internal fun List<String>.toFileNameExtensionFilter(): FileNameExtensionFilter {
  val extensions = flatMap { mime ->
    when (mime) {
      "image/*" -> listOf("png", "jpg", "jpeg", "webp", "gif", "bmp")
      "video/*" -> listOf("mp4", "mkv", "avi", "mov", "webm")
      "application/pdf" -> listOf("pdf")
      "application/x-sqlite3", "application/vnd.sqlite3" -> listOf("db", "sqlite", "sqlite3")
      "*/*" -> return FileNameExtensionFilter("All files", "*")
      else -> emptyList()
    }
  }
  val description = extensions.joinToString(", ") { "*.$it" }
  return FileNameExtensionFilter(description, *extensions.toTypedArray())
}
