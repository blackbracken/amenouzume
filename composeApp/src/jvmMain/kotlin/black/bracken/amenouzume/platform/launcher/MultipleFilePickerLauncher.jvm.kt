package black.bracken.amenouzume.platform.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import javax.swing.JFileChooser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberMultipleFilePickerLauncher(
  mimeTypes: List<String>,
  onResult: (List<String>) -> Unit,
): () -> Unit {
  val scope = rememberCoroutineScope()
  return {
    scope.launch(Dispatchers.IO) {
      var paths: List<String> = emptyList()
      java.awt.EventQueue.invokeAndWait {
        val chooser = JFileChooser().apply {
          fileSelectionMode = JFileChooser.FILES_ONLY
          isMultiSelectionEnabled = true
          fileFilter = mimeTypes.toFileNameExtensionFilter()
        }
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
          paths = chooser.selectedFiles.map { it.absolutePath }
        }
      }
      withContext(Dispatchers.Main) {
        onResult(paths)
      }
    }
  }
}
