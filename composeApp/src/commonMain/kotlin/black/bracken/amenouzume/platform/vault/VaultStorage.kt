package black.bracken.amenouzume.platform.vault

expect class VaultStorage {
  suspend fun createDatabaseFile(absolutePath: String)
}
