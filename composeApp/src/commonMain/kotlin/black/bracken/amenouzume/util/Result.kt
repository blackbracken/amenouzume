package black.bracken.amenouzume.util

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_unexpected
import black.bracken.amenouzume.kernel.error.AppFailure
import org.jetbrains.compose.resources.StringResource

fun <T> Result<T>.handleFailureWithMessage(block: (StringResource) -> Unit): Result<T> {
  onFailure { e ->
    if (e !is AppFailure) e.printStackTrace()
    block((e as? AppFailure)?.messageRes ?: Res.string.error_unexpected)
  }
  return this
}
