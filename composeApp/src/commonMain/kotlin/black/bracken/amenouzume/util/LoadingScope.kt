package black.bracken.amenouzume.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoadingScope {
  private var trackedCount by mutableIntStateOf(0)

  /** [isLoading] is backed by [mutableIntStateOf]. */
  val isLoading get() = trackedCount > 0

  /**
   * Runs [block] while keeping [isLoading] true, even across multiple concurrent calls.
   */
  suspend fun track(block: suspend () -> Unit) {
    trackedCount++
    try {
      block()
    } finally {
      trackedCount--
    }
  }
}

/** Launches [block] on [ViewModel.viewModelScope] while keeping [LoadingScope.isLoading] true. */
context(viewModel: ViewModel)
fun LoadingScope.launchTracked(block: suspend () -> Unit) {
  viewModel.viewModelScope.launch { track(block) }
}
