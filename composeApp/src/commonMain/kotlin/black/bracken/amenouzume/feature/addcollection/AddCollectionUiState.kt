package black.bracken.amenouzume.feature.addcollection

import black.bracken.amenouzume.uishared.ScreenUiState
import org.jetbrains.compose.resources.StringResource

data class AddCollectionUiState(
  override val isBusy: Boolean,
  val title: String,
  val category: String,
  val errorMessage: StringResource?,
) : ScreenUiState
