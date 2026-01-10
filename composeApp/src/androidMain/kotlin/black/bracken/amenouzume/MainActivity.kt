package black.bracken.amenouzume

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import black.bracken.amenouzume.db.DatabaseDriverFactory
import black.bracken.amenouzume.db.createDatabase
import black.bracken.amenouzume.repository.CollectionRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val database = createDatabase(DatabaseDriverFactory(applicationContext))
        val repository = CollectionRepository(database)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}