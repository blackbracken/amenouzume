package black.bracken.amenouzume.platform.launcher

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFilePickerLauncher(onResult: (String?) -> Unit): () -> Unit
