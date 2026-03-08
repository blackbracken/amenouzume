package black.bracken.amenouzume.platform.file

import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
actual fun rememberDirectoryPickerLauncher(onResult: (String?) -> Unit): () -> Unit {
  val launcher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
      onResult(uri?.toFilePath())
    }
  return { launcher.launch(null) }
}

private fun Uri.toFilePath(): String? {
  val docId = DocumentsContract.getTreeDocumentId(this) ?: return null

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
