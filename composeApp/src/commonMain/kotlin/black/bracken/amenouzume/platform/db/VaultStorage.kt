package black.bracken.amenouzume.platform.db

expect class VaultStorage {
  suspend fun createDatabaseFile(absolutePath: String)
}
