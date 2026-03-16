package black.bracken.amenouzume.platform.launcher

import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
actual fun rememberMultipleFilePickerLauncher(
  mimeTypes: List<String>,
  onResult: (List<String>) -> Unit,
): () -> Unit {
  val launcher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
      onResult(uris.mapNotNull { it.toDocumentFilePath() })
    }
  return { launcher.launch(mimeTypes.toTypedArray()) }
}

private fun Uri.toDocumentFilePath(): String? {
  val docId = DocumentsContract.getDocumentId(this) ?: return null

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
