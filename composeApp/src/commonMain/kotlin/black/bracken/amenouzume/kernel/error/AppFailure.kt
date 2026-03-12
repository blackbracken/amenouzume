package black.bracken.amenouzume.kernel.error

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.error_unexpected
import org.jetbrains.compose.resources.StringResource

abstract class AppFailure : Exception() {
  abstract val messageRes: StringResource
}

class CommonFailure(override val messageRes: StringResource = Res.string.error_unexpected) : AppFailure()
