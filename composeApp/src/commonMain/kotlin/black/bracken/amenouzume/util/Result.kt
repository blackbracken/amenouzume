package black.bracken.amenouzume.util

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_unexpected
import black.bracken.amenouzume.kernel.error.AppFailure
import kotlinx.coroutines.CancellationException
import org.jetbrains.compose.resources.StringResource

fun <T> Result<T>.handleFailureWithMessage(block: (StringResource) -> Unit): Result<T> {
  onFailure { e ->
    when (e) {
      is CancellationException -> return@onFailure
      is AppFailure -> block(e.messageRes)
      else -> {
        e.printStackTrace()
        block(Res.string.error_unexpected)
      }
    }
  }
  return this
}
