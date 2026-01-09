package black.bracken.amenouzume

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform