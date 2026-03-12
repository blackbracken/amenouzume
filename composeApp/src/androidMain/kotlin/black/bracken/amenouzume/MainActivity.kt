package black.bracken.amenouzume

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    val graph = (application as AmenouzumeApplication).graph
    setContent {
      App(graph.metroViewModelFactory, graph.navigator)
    }
  }
}
