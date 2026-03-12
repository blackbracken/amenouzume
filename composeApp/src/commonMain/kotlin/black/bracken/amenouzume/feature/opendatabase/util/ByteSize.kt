package black.bracken.amenouzume.feature.opendatabase.util

fun Long.toSizeText(): String =
  when {
    this >= 1024L * 1024 -> "${"%.1f".format(this / (1024.0 * 1024.0))} MB"
    this >= 1024 -> "${"%.1f".format(this / 1024.0)} KB"
    else -> "$this B"
  }
