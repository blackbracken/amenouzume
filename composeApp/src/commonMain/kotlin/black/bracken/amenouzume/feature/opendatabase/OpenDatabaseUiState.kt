package black.bracken.amenouzume.feature.opendatabase

import black.bracken.amenouzume.uishared.ScreenUiState
import org.jetbrains.compose.resources.StringResource

data class OpenDatabaseUiState(
    val databases: List<OpenDatabaseEntry>? = null,
    override val isBusy: Boolean = false,
    val errorMessage: StringResource? = null,
) : ScreenUiState

data class OpenDatabaseEntry(
    val name: String,
    val path: String,
    val size: String,
)
