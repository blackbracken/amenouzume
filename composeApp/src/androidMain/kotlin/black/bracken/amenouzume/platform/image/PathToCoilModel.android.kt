package black.bracken.amenouzume.platform.image

import android.net.Uri

actual fun pathToCoilModel(path: String): Any = Uri.parse(path)
