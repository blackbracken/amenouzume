package black.bracken.amenouzume.util

import kotlin.time.Clock
import kotlin.time.Instant

object TimeProvider {
  private var provider: () -> Instant = { Clock.System.now() }

  fun now(): Instant = provider()

  fun override(provider: () -> Instant) {
    this.provider = provider
  }

  fun reset() {
    provider = { Clock.System.now() }
  }
}
