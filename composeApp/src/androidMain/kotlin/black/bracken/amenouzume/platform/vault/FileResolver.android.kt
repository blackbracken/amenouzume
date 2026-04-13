package black.bracken.amenouzume.platform.vault

import android.content.Context
import android.net.Uri
import java.io.File

actual class FileResolver(
  private val context: Context,
) {
  actual fun copyPickedFile(sourcePath: String, destFile: File) {
    val uri = Uri.parse(sourcePath)
    context.contentResolver.openInputStream(uri)?.use { input ->
      destFile.outputStream().use { output ->
        input.copyTo(output)
      }
    } ?: throw IllegalStateException("Cannot open input stream for: $sourcePath")
  }

  actual fun getExtension(sourcePath: String): String {
    val uri = Uri.parse(sourcePath)
    val mimeType = context.contentResolver.getType(uri)
    if (mimeType != null) {
      val ext = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
      if (!ext.isNullOrEmpty()) return ext
    }
    return uri.lastPathSegment?.substringAfterLast('.', "") ?: ""
  }
}
