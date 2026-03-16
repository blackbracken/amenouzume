package black.bracken.amenouzume.feature.opendatabase

import black.bracken.amenouzume.kernel.model.VaultHistory
import black.bracken.amenouzume.uishared.ScreenUiState
import black.bracken.amenouzume.util.Loadable
import org.jetbrains.compose.resources.StringResource

data class OpenDatabaseUiState(
  override val isBusy: Boolean,
  val databases: Loadable<List<OpenDatabaseEntry>>,
  val errorMessage: StringResource?,
) : ScreenUiState

data class OpenDatabaseEntry(
  val name: String,
  val path: String,
) {
  companion object {
    fun from(history: VaultHistory) = OpenDatabaseEntry(name = history.name, path = history.path)
  }
}
