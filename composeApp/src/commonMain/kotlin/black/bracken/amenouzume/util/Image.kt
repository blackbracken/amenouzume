package black.bracken.amenouzume.util

import androidx.compose.ui.unit.Dp

private const val DP_TO_PX_SCALE = 3

fun resolvePixelSize(size: Dp): Int = (size.value * DP_TO_PX_SCALE).toInt()
