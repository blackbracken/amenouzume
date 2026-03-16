package black.bracken.amenouzume.uishared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun DashedBorderArea(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit,
) {
  val primary = MaterialTheme.colorScheme.primary
  val shape = MaterialTheme.shapes.large
  Box(
    modifier = modifier
      .fillMaxWidth()
      .drawBehind {
        drawRoundRect(
          color = primary.copy(alpha = 0.2f),
          style = Stroke(
            width = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12.dp.toPx(), 8.dp.toPx())),
          ),
          cornerRadius = CornerRadius(16.dp.toPx()),
        )
      }.background(primary.copy(alpha = 0.05f), shape)
      .padding(32.dp),
    contentAlignment = Alignment.Center,
    content = content,
  )
}
