package black.bracken.amenouzume.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
class LoadingScope {
  private var trackedCount by mutableIntStateOf(0)

  /** Backed by [mutableIntStateOf]. */
  val isLoading get() = trackedCount > 0

  /** Runs [block] while keeping [isLoading] true, even across multiple concurrent calls. */
  suspend fun track(block: suspend () -> Unit) {
    trackedCount++
    try {
      block()
    } finally {
      trackedCount--
    }
  }
}
