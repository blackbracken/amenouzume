package black.bracken.amenouzume.platform.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
expect fun rememberDirectoryPickerLauncher(onResult: (String?) -> Unit): () -> Unit

@Suppress("SuspiciousCallableReferenceInLambda")
val LocalDirectoryPickerLauncher =
  staticCompositionLocalOf<@Composable ((String?) -> Unit) -> () -> Unit> {
    ::rememberDirectoryPickerLauncher
  }
