package black.bracken.amenouzume.util

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_unexpected
import black.bracken.amenouzume.kernel.error.AppFailure
import org.jetbrains.compose.resources.StringResource

sealed interface Loadable<out T> {
  data object Loading : Loadable<Nothing>

  data class Loaded<T>(
    val value: T,
  ) : Loadable<T>

  data class Failed(
    val cause: Exception,
  ) : Loadable<Nothing> {
    val messageRes: StringResource
      get() = when (cause) {
        is AppFailure -> cause.messageRes
        else -> Res.string.error_unexpected
      }
  }
}

fun <T> Loadable<T>.getOrNull(): T? =
  when (this) {
    is Loadable.Loaded -> value
    else -> null
  }

inline fun <T, R> Loadable<T>.map(transform: (T) -> R): Loadable<R> =
  when (this) {
    is Loadable.Loading, is Loadable.Failed -> this
    is Loadable.Loaded -> Loadable.Loaded(transform(value))
  }

fun <T> Result<T>.toLoadable(): Loadable<T> =
  fold(
    onSuccess = { Loadable.Loaded(it) },
    onFailure = { Loadable.Failed(if (it is Exception) it else RuntimeException(it)) },
  )
