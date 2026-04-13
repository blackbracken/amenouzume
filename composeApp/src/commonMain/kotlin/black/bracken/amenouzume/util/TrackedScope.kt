package black.bracken.amenouzume.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackedScope {
  private var trackedCount by mutableIntStateOf(0)

  /** Backed by [mutableIntStateOf]. */
  val isRunning get() = trackedCount > 0

  suspend fun <T> track(block: suspend () -> T): T {
    withContext(Dispatchers.Main.immediate) { trackedCount++ }
    return try {
      block()
    } finally {
      withContext(Dispatchers.Main.immediate) { trackedCount-- }
    }
  }
}
