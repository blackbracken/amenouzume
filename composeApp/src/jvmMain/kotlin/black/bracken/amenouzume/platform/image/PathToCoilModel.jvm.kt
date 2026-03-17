package black.bracken.amenouzume.platform.image

import okio.Path.Companion.toPath

actual fun pathToCoilModel(path: String): Any = path.toPath()
