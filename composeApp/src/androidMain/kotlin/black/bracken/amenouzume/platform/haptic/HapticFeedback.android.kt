package black.bracken.amenouzume.platform.haptic

import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberHapticFeedback(): (AppHapticFeedbackType) -> Unit {
  val view = LocalView.current
  return remember(view) {
    { type ->
      val constant = when (type) {
        AppHapticFeedbackType.LightTap -> HapticFeedbackConstants.CONTEXT_CLICK
        AppHapticFeedbackType.LongPress -> HapticFeedbackConstants.LONG_PRESS
      }
      view.performHapticFeedback(constant)
    }
  }
}
