package black.bracken.amenouzume.kernel.model

data class CollectionFile(
  val filePath: String,
  val fileType: CollectionFileType,
  val displayOrder: Int,
)
