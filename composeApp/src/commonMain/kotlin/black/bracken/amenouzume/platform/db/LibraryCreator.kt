package black.bracken.amenouzume.platform.db

expect class LibraryCreator {
  suspend fun create(path: String): Result<Unit>
}
