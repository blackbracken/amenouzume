package black.bracken.amenouzume.platform.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
expect fun rememberFilePickerLauncher(onResult: (String?) -> Unit): () -> Unit

@Suppress("SuspiciousCallableReferenceInLambda")
val LocalFilePickerLauncher =
  staticCompositionLocalOf<@Composable ((String?) -> Unit) -> () -> Unit> {
    ::rememberFilePickerLauncher
  }
