package black.bracken.amenouzume.uishared.navigation

import black.bracken.amenouzume.kernel.model.CollectionId
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute

@Serializable
data object OpenDatabaseRoute : AppRoute

@Serializable
data class CollectionListRoute(
  val vaultPath: String,
) : AppRoute

@Serializable
data class AddCollectionRoute(
  val vaultPath: String,
) : AppRoute

@Serializable
data object ManageTagRoute : AppRoute

@Serializable
data object ManageAuthorRoute : AppRoute

@Serializable
data class CollectionViewerRoute(
  val collectionId: CollectionId,
) : AppRoute
