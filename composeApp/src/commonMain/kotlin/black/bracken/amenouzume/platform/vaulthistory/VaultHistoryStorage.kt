package black.bracken.amenouzume.platform.vaulthistory

expect class VaultHistoryStorage {
  suspend fun loadPaths(): List<String>

  suspend fun addPath(path: String)

  suspend fun removePath(path: String)
}
