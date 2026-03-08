package black.bracken.amenouzume.platform.file

import androidx.compose.runtime.Composable

@Composable
expect fun rememberDirectoryPickerLauncher(onResult: (String?) -> Unit): () -> Unit
