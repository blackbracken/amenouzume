package black.bracken.amenouzume.uishared.navigation

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
