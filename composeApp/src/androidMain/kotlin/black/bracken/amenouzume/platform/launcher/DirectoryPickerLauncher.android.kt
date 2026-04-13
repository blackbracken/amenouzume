package black.bracken.amenouzume.platform.launcher

import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import black.bracken.amenouzume.util.resolveDocumentPath

@Composable
actual fun rememberDirectoryPickerLauncher(onResult: (String?) -> Unit): () -> Unit {
  val launcher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
      val docId = uri?.let { DocumentsContract.getTreeDocumentId(it) }
      onResult(docId?.let { resolveDocumentPath(it) })
    }
  return { launcher.launch(null) }
}
