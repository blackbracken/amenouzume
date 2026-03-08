package black.bracken.amenouzume.platform.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.swing.JFileChooser

@Composable
actual fun rememberDirectoryPickerLauncher(onResult: (String?) -> Unit): () -> Unit {
  val scope = rememberCoroutineScope()
  return {
    scope.launch(Dispatchers.IO) {
      var path: String? = null
      java.awt.EventQueue.invokeAndWait {
        val chooser =
          JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
          }
        val result = chooser.showOpenDialog(null)
        path = if (result == JFileChooser.APPROVE_OPTION) chooser.selectedFile?.absolutePath else null
      }
      withContext(Dispatchers.Main) {
        onResult(path)
      }
    }
  }
}
