package black.bracken.amenouzume.platform.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

enum class AppHapticFeedbackType {
  LightTap,
  LongPress,
}

@Composable
expect fun rememberHapticFeedback(): (AppHapticFeedbackType) -> Unit

@Suppress("SuspiciousCallableReferenceInLambda")
val LocalHapticFeedback =
  staticCompositionLocalOf<@Composable () -> (AppHapticFeedbackType) -> Unit> {
    ::rememberHapticFeedback
  }
