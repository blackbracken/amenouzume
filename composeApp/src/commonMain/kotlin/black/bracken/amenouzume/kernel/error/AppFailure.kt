package black.bracken.amenouzume.kernel.error

import org.jetbrains.compose.resources.StringResource

abstract class AppFailure : Exception() {
  abstract val messageRes: StringResource
}

class CommonFailure(
  override val messageRes: StringResource,
) : AppFailure()
