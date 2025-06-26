package com.me.animedownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Airplay
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.outlined.Airplay
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.me.animedownloader.ui.theme.AnimeDownloaderTheme

data class BottomNavItem(
    val title: String,
    val selectedItem: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val downloadThreads = ArrayList<Thread>()
        val stopThreads = ArrayList<Thread>()
        //Controller().loadAnimeList(downloadThreads, stopThreads, this@MainActivity, this)

        setContent {
            AnimeDownloaderTheme {
                var items = listOf<BottomNavItem>(
                    BottomNavItem(
                        title = "Anime",
                        selectedItem = Icons.Filled.Archive,
                        unselectedIcon = Icons.Outlined.Archive,
                        hasNews = false
                    ),
                    BottomNavItem(
                        title = "Episodi",
                        selectedItem = Icons.Filled.Dashboard,
                        unselectedIcon = Icons.Outlined.Dashboard,
                        hasNews = false
                    ),
                    BottomNavItem(
                        title = "Guarda",
                        selectedItem = Icons.Filled.Airplay,
                        unselectedIcon = Icons.Outlined.Airplay,
                        hasNews = false
                    )
                )
                var selectedItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold (
                        bottomBar = {
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        onClick = {
                                            selectedItemIndex = index
                                            // navController.navigate(item.title)
                                        },
                                        label = {
                                            Text(text = item.title)
                                        },
                                        icon = {
                                            BadgedBox(
                                                badge = {
                                                    if(item.badgeCount != null)
                                                        Badge{
                                                            Text(text = item.badgeCount.toString())
                                                        }
                                                    else if(item.hasNews)
                                                        Badge()
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if(selectedItemIndex == index) item.selectedItem else item.unselectedIcon,
                                                    contentDescription = item.title
                                                )
                                            }
                                        },
                                        alwaysShowLabel = false
                                    )
                                }
                            }
                        }
                    ) {  }
                }
            }
        }
    }
}

@Composable fun AnimeScreen() { Text("Anime presenti nel txt") }
@Composable fun EpisodeScreen() { Text("Episodi usciti") }
@Composable fun AvailableListScreen() { Text("Episodi disponibili per essere visti") }
