package black.bracken.amenouzume.platform.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberFilePickerLauncher(onResult: (String?) -> Unit): () -> Unit {
  val scope = rememberCoroutineScope()
  return {
    scope.launch(Dispatchers.IO) {
      var path: String? = null
      java.awt.EventQueue.invokeAndWait {
        val chooser =
          JFileChooser().apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            fileFilter = FileNameExtensionFilter("SQLite Database (*.db)", "db")
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
