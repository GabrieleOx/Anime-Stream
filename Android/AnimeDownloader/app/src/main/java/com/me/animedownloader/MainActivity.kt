package com.me.animedownloader

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Airplay
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Airplay
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.documentfile.provider.DocumentFile
import com.me.animedownloader.AnimeFinder
import com.me.animedownloader.MainActivity.Companion.saveDirectory
import com.me.animedownloader.ui.theme.AnimeDownloaderTheme
import java.io.OutputStream

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
    private val viewModel by viewModels<EpSelectionViewModel>()

    companion object{
        @JvmStatic
        var anime: ArrayList<String> = ArrayList()

        @JvmStatic
        var episodi: List<String>? = null

        @JvmStatic
        var nEpisodes: ArrayList<Int> = ArrayList()

        @JvmStatic
        var startVals: java.util.ArrayList<Int> = java.util.ArrayList<Int>()

        @JvmStatic
        var abslouteITA: ArrayList<Boolean> = ArrayList()

        @JvmStatic
        var saveDirectory: DocumentFile? = null

        @JvmStatic
        var animeSceltoFolder: DocumentFile? = null
    }

    lateinit var pickTxt: PickTxt
    lateinit var pickDirectory: PickDirectory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        pickTxt  = PickTxt(this, contentResolver, this@MainActivity)
        pickDirectory = PickDirectory(this, this@MainActivity)

        val prefs = this@MainActivity.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val txtUri = prefs.getString("persisted_uri", null)

        if(txtUri != null){
            anime = AnimeFinder.getAnime(nEpisodes, abslouteITA, startVals, this, txtUri)
        }

        val folderuri = pickDirectory.getPersistedFolderUri()

        if(folderuri != null){
            saveDirectory = creaSeNonEsiste(this@MainActivity, folderuri, "AnimeDownload")
        }

            setContent {
            AnimeDownloaderTheme {
                val (selectedAnime, onAnimeSelected) = remember { mutableStateOf(String()) }
                val scope = rememberCoroutineScope()
                val (isAlive, onSearching) = remember { mutableStateOf(false) }
                val (epCount, onEpFind) = remember { mutableStateOf(0) }
                val items = listOf<BottomNavItem>(
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
                        badgeCount = epCount,
                        hasNews = false
                    ),
                    BottomNavItem(
                        title = "Guarda",
                        selectedItem = Icons.Filled.Airplay,
                        unselectedIcon = Icons.Outlined.Airplay,
                        hasNews = false
                    ),
                    BottomNavItem(
                        title = "Settings",
                        selectedItem = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings,
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
                                        },
                                        label = {
                                            Text(text = item.title)
                                        },
                                        icon = {
                                            BadgedBox(
                                                badge = {
                                                    if(item.badgeCount != null && item.badgeCount > 0)
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
                            AnimeScreen(innerPadding, anime, viewModel, this, selectedAnime, onAnimeSelected, isAlive, onSearching, scope, onEpFind)
                        else if(selectedItemIndex == 1)
                            EpisodeScreen(innerPadding, isAlive)
                        else if(selectedItemIndex == 2)
                            AvailableListScreen(innerPadding)
                        else SettingsScreen(innerPadding, pickTxt, pickDirectory)
                    }
                }
            }
        }
    }
}

fun creaSeNonEsiste(
    context: Context,
    folderUri: Uri,
    folderName: String
): DocumentFile? {
    val directory = DocumentFile.fromTreeUri(context, folderUri) ?: return null

    val exists = directory?.findFile(folderName)

    return if (exists == null || !exists.isDirectory) {
        directory.createDirectory(folderName)
    }else {
        exists
    }
}

fun creaSeNonEsiste(
    father: DocumentFile,
    folderName: String
): DocumentFile? {
    val exists = father.findFile(folderName)

    return if (exists == null || !exists.isDirectory) {
        father.createDirectory(folderName)
    }else {
        exists
    }
}

@SuppressLint("Recycle")
fun creaOutputStream(
    contesto: Context,
    fatherFolder: DocumentFile,
    fileName: String,
    fileType: String
): OutputStream?{
    val newFile = fatherFolder.createFile(fileType, fileName)

    if(newFile != null){
        val outputStream = contesto.contentResolver.openOutputStream(newFile.uri)
        return outputStream
    }
    return null
}