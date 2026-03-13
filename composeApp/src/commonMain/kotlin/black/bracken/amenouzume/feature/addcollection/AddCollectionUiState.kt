package black.bracken.amenouzume.feature.addcollection

import black.bracken.amenouzume.uishared.ScreenUiState
import org.jetbrains.compose.resources.StringResource

data class AddCollectionUiState(
  val title: String,
  val category: String,
  override val isBusy: Boolean,
  val errorMessage: StringResource?,
) : ScreenUiState
