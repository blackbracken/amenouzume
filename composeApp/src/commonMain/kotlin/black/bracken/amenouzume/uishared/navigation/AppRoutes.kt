package black.bracken.amenouzume.uishared.navigation

import kotlinx.serialization.Serializable

@Serializable
data object OpenDatabaseRoute

@Serializable
data class CollectionListRoute(val vaultPath: String)
