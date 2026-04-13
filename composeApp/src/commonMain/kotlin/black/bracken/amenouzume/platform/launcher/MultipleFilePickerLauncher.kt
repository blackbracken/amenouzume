package black.bracken.amenouzume.platform.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
expect fun rememberMultipleFilePickerLauncher(
  mimeTypes: List<String>,
  onResult: (sourceLocations: List<String>) -> Unit,
): () -> Unit

@Suppress("SuspiciousCallableReferenceInLambda")
val LocalMultipleFilePickerLauncher =
  staticCompositionLocalOf<@Composable (List<String>, (List<String>) -> Unit) -> () -> Unit> {
    ::rememberMultipleFilePickerLauncher
  }
