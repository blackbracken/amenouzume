package black.bracken.amenouzume.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun resolvePixelSize(size: Dp): Int {
  val density = LocalDensity.current.density
  return (size.value * density).toInt()
}
