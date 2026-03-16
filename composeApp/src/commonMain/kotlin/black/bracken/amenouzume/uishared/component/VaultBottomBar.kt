package black.bracken.amenouzume.uishared.component

import amenouzume.composeapp.generated.resources.Res
import amenouzume.composeapp.generated.resources.collection_list_tab_add
import amenouzume.composeapp.generated.resources.collection_list_tab_collections
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.stringResource

enum class VaultTab { COLLECTIONS, ADD }

@Composable
fun VaultBottomBar(
  selectedTab: VaultTab,
  onSelectTab: (VaultTab) -> Unit,
) {
  NavigationBar {
    NavigationBarItem(
      selected = selectedTab == VaultTab.COLLECTIONS,
      onClick = { onSelectTab(VaultTab.COLLECTIONS) },
      icon = {
        Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = null)
      },
      label = {
        Text(
          text = stringResource(Res.string.collection_list_tab_collections),
          fontWeight = if (selectedTab == VaultTab.COLLECTIONS) FontWeight.Bold else FontWeight.Normal,
        )
      },
      colors = NavigationBarItemDefaults.colors(
        indicatorColor = Color.Transparent,
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
      ),
    )
    NavigationBarItem(
      selected = selectedTab == VaultTab.ADD,
      onClick = { onSelectTab(VaultTab.ADD) },
      icon = {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
      },
      label = {
        Text(
          text = stringResource(Res.string.collection_list_tab_add),
          fontWeight = if (selectedTab == VaultTab.ADD) FontWeight.Bold else FontWeight.Normal,
        )
      },
      colors = NavigationBarItemDefaults.colors(
        indicatorColor = Color.Transparent,
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
      ),
    )
  }
}
