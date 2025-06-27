package com.me.animedownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Airplay
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.outlined.Airplay
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.me.animedownloader.ui.theme.AnimeDownloaderTheme
import kotlinx.coroutines.selects.select
import java.nio.file.WatchEvent

data class BottomNavItem(
    val title: String,
    val selectedItem: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

val BitcountFontFamily = FontFamily(
    Font(R.font.bitcountgriddouble, FontWeight.Normal)
)

val ComicSansFontFamily = FontFamily(
    Font(R.font.comic_sans_ms, FontWeight.Normal)
)

val PressStart2PFontFamily = FontFamily(
    Font(R.font.pressstart2p_regular, FontWeight.Normal)
)


class MainActivity : ComponentActivity() {

    companion object{
        @JvmStatic
        var anime: ArrayList<String> = ArrayList()

        @JvmStatic
        var episodi: java.util.ArrayList<String> = ArrayList()

        @JvmStatic
        var nEpisodes: ArrayList<Int> = ArrayList()

        @JvmStatic
        var startVals: java.util.ArrayList<Int> = java.util.ArrayList<Int>()

        @JvmStatic
        var abslouteITA: ArrayList<Boolean> = ArrayList()

        @JvmStatic
        var findEpisodes: Thread = Thread()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val downloadThreads = ArrayList<Thread>()
        val stopThreads = ArrayList<Thread>()
        AnimeFinder.getAnime(anime, nEpisodes, abslouteITA, startVals, this, this@MainActivity)

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
                    ) { innerPadding ->
                        if(selectedItemIndex == 0)
                            AnimeScreen(innerPadding, anime)
                        else if(selectedItemIndex == 1)
                            EpisodeScreen(innerPadding)
                        else AvailableListScreen(innerPadding)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimeScreen(p: PaddingValues, animeNames: ArrayList<String>) {
    val (selectedAnime, onAnimeSelected) = remember { mutableStateOf(String()) }
    var lastSelected by remember { mutableStateOf(String()) }
    Column (
        modifier = Modifier
            .background(Color(185, 131, 242))
            .padding(p)
            .padding(vertical = 15.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Anime disponibili:",
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PressStart2PFontFamily
        )
        LazyColumn (
            modifier = Modifier
                .selectableGroup()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(animeNames){ index, currentName ->
                Row (
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .selectable(
                            selected = currentName == selectedAnime,
                            onClick = {
                                try {
                                    if (MainActivity.findEpisodes.isAlive) {
                                    } else {
                                        onAnimeSelected(currentName)
                                        //loadEpisodeList(index, downloadThreads, stopThreads, contesto)
                                    }
                                } catch (e1: InterruptedException) {
                                    e1.printStackTrace()
                                }
                            },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = currentName == selectedAnime,
                        onClick = null
                    )
                    Text(
                        text = getAnimeName(currentName),
                        fontSize = 18.sp,
                        fontFamily = ComicSansFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun EpisodeScreen(p: PaddingValues) {
    Text(
        text = "Episodi usciti",
        modifier = Modifier.padding(p)

    )
}
@Composable
fun AvailableListScreen(p: PaddingValues) {
    Text(
        text = "Episodi scaricati",
        modifier = Modifier.padding(p)
    )
}
