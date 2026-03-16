package black.bracken.amenouzume.feature.addcollection

import black.bracken.amenouzume.feature.collectionlist.CollectionCategory
import black.bracken.amenouzume.uishared.ScreenUiState
import org.jetbrains.compose.resources.StringResource

data class AddCollectionUiState(
  override val isBusy: Boolean,
  val selectedCategory: CollectionCategory?,
  val title: String,
  val authors: List<String>,
  val tags: List<String>,
  val isPublic: Boolean,
  val errorMessage: StringResource?,
) : ScreenUiState
