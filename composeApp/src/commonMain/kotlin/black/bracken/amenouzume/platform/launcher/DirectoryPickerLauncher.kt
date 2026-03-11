package black.bracken.amenouzume.platform.launcher

import androidx.compose.runtime.Composable

@Composable
expect fun rememberDirectoryPickerLauncher(onResult: (String?) -> Unit): () -> Unit
