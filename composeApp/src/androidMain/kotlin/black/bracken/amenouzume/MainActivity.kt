package black.bracken.amenouzume

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    val graph = (application as AmenouzumeApplication).graph
    setContent {
      val backStack by graph.navigator.backStack.collectAsStateWithLifecycle()

      BackHandler { graph.navigator.back() }

      LaunchedEffect(backStack) {
        if (backStack.isEmpty()) finish()
      }

      App(graph.metroViewModelFactory, graph.navigator)
    }
  }
}
