package black.bracken.amenouzume.platform.launcher

import androidx.compose.runtime.Composable

@Composable
expect fun rememberMultipleFilePickerLauncher(
  mimeTypes: List<String>,
  onResult: (List<String>) -> Unit,
): () -> Unit
