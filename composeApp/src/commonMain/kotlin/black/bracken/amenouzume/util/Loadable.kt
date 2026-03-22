package black.bracken.amenouzume.util

import black.bracken.amenouzume.kernel.error.AppFailure
import black.bracken.amenouzume.kernel.error.CommonFailure
import kotlinx.coroutines.CancellationException
import org.jetbrains.compose.resources.StringResource

sealed interface Loadable<out T> {
  data object Loading : Loadable<Nothing>

  data class Loaded<T>(
    val value: T,
  ) : Loadable<T>

  data class Failed(
    val messageRes: StringResource,
  ) : Loadable<Nothing>

  companion object {
    suspend fun <T> from(block: suspend () -> T): Loadable<T> =
      try {
        Loaded(block())
      } catch (e: CancellationException) {
        throw e
      } catch (e: AppFailure) {
        Failed(e.messageRes)
      }
  }
}

fun <T> Loadable<T>.getOrThrow(): T =
  when (this) {
    is Loadable.Loaded -> value
    is Loadable.Failed -> throw CommonFailure(messageRes)
    is Loadable.Loading -> error("unreachable: Loading state must be resolved before calling getOrThrow")
  }

inline fun <T, R> Loadable<T>.map(transform: (T) -> R): Loadable<R> =
  when (this) {
    is Loadable.Loading, is Loadable.Failed -> this
    is Loadable.Loaded -> Loadable.Loaded(transform(value))
  }
