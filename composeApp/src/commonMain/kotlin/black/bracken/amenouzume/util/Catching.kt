package black.bracken.amenouzume.util

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_unexpected
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.amenouzume.kernel.error.AppFailure
import black.bracken.amenouzume.kernel.error.CommonFailure
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Suppress("unused")
fun runWithCatching(
  onFailure: (AppFailure) -> Unit,
  block: () -> Unit,
) {
  try {
    block()
  } catch (e: AppFailure) {
    onFailure(e)
  } catch (e: Exception) {
    e.printStackTrace()
    onFailure(CommonFailure(Res.string.error_unexpected))
  }
}

context(viewModel: ViewModel)
fun launchWithCatching(
  onFailure: (AppFailure) -> Unit,
  block: suspend () -> Unit,
) {
  viewModel.viewModelScope.launch {
    try {
      block()
    } catch (e: CancellationException) {
      throw e
    } catch (e: AppFailure) {
      onFailure(e)
    } catch (e: Exception) {
      e.printStackTrace()
      onFailure(CommonFailure(Res.string.error_unexpected))
    }
  }
}
