package black.bracken.amenouzume.platform.launcher

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
      onResult(uris.map { it.toString() })
    }
  return { launcher.launch(mimeTypes.toTypedArray()) }
}
