package black.bracken.amenouzume.feature.opendatabase

import black.bracken.amenouzume.uishared.ScreenUiState
import black.bracken.amenouzume.util.Loadable
import org.jetbrains.compose.resources.StringResource

data class OpenDatabaseUiState(
    val databases: Loadable<List<OpenDatabaseEntry>>,
    override val isBusy: Boolean,
    val errorMessage: StringResource?,
) : ScreenUiState

data class OpenDatabaseEntry(
    val name: String,
    val path: String,
    val size: String,
)
