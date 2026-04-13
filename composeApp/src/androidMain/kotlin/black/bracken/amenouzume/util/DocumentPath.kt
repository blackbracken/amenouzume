package black.bracken.amenouzume.util

import android.os.Environment

fun resolveDocumentPath(docId: String): String? {
  val parts = docId.split(":", limit = 2)
  val volumeId = parts.firstOrNull() ?: return null
  val relativePath = parts.getOrNull(1)

  val volumePath =
    if (volumeId.equals("primary", ignoreCase = true)) {
      Environment.getExternalStorageDirectory().absolutePath
    } else {
      "/storage/$volumeId"
    }

  return if (relativePath.isNullOrEmpty()) volumePath else "$volumePath/$relativePath"
}
