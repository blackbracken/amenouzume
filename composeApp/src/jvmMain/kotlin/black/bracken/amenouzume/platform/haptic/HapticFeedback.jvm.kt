package black.bracken.amenouzume.platform.haptic

import androidx.compose.runtime.Composable

@Composable
actual fun rememberHapticFeedback(): (AppHapticFeedbackType) -> Unit = {}
