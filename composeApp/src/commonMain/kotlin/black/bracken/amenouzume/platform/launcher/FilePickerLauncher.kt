package black.bracken.amenouzume.platform.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
expect fun rememberFilePickerLauncher(
  mimeTypes: List<String>,
  onResult: (String?) -> Unit,
): () -> Unit

@Suppress("SuspiciousCallableReferenceInLambda")
val LocalFilePickerLauncher =
  staticCompositionLocalOf<@Composable (List<String>, (String?) -> Unit) -> () -> Unit> {
    ::rememberFilePickerLauncher
  }
