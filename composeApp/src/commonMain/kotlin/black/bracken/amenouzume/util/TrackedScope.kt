package black.bracken.amenouzume.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

class TrackedScope {
  private var trackedCount by mutableIntStateOf(0)

  /** Backed by [mutableIntStateOf]. */
  val isRunning get() = trackedCount > 0

  suspend fun <T> track(block: suspend () -> T): T {
    trackedCount++
    return try {
      block()
    } finally {
      trackedCount--
    }
  }
}
