package black.bracken.amenouzume.platform.haptic

import androidx.compose.runtime.Composable

enum class AppHapticFeedbackType {
  LightTap,
  LongPress,
}

@Composable
expect fun rememberHapticFeedback(): (AppHapticFeedbackType) -> Unit
