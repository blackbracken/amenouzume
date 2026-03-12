package black.bracken.amenouzume.feature.addcollection

import org.jetbrains.compose.resources.StringResource

data class AddCollectionUiState(
  val title: String = "",
  val category: String = "",
  val isLoading: Boolean = false,
  val errorMessage: StringResource? = null,
)
