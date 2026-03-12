package black.bracken.amenouzume.feature.opendatabase.util
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteSizeTest {
  @Test
  fun bytes() {
    assertEquals("0 B", 0L.toSizeText())
    assertEquals("1 B", 1L.toSizeText())
    assertEquals("1023 B", 1023L.toSizeText())
  }

  @Test
  fun kilobytes() {
    assertEquals("1.0 KB", 1024L.toSizeText())
    assertEquals("1.5 KB", 1536L.toSizeText())
    assertEquals("1024.0 KB", (1024L * 1024 - 1).toSizeText())
  }

  @Test
  fun megabytes() {
    assertEquals("1.0 MB", (1024L * 1024).toSizeText())
    assertEquals("1.5 MB", (1024L * 1024 * 3 / 2).toSizeText())
    assertEquals("10.0 MB", (1024L * 1024 * 10).toSizeText())
  }
}
