package com.me.animedownloader

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.me.animedownloader.ui.theme.AnimeDownloaderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.ExecutionException

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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val downloadThreads = ArrayList<Thread>()
        val stopThreads = ArrayList<Thread>()
        AnimeFinder.getAnime(anime, nEpisodes, abslouteITA, startVals, this, this@MainActivity)

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
                            EpisodeScreen(innerPadding, scope, isAlive)
                        else AvailableListScreen(innerPadding)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimeScreen(
    p: PaddingValues,
    animeNames: List<String>,
    viewModel: EpSelectionViewModel,
    contesto: Context,
    selectedAnime: String,
    onAnimeSelected:(String) -> Unit,
    isAlive: Boolean,
    onSearching:(Boolean) -> Unit,
    scope: CoroutineScope,
    onEpFind:(Int) -> Unit
) {
    var indexAnimeScelto by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Image(
            painter = painterResource(R.drawable.sukuna),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = 100.dp)
        )

        Column (
            modifier = Modifier
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
                                        if (!isAlive) {
                                            onAnimeSelected(currentName)
                                            indexAnimeScelto = index
                                            viewModel.onAnimeChoose()
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
                            fontFamily = ComicSansFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    if(viewModel.isDialogShown){
        SelectionDialog (
            onDismiss = {
                viewModel.onDismissDialog()
                loadEpisodeList(indexAnimeScelto, 0, contesto, onSearching, scope, onEpFind)
            },
            onConfirm = { scelto ->
                viewModel.onDismissDialog()
                loadEpisodeList(indexAnimeScelto, scelto, contesto, onSearching, scope, onEpFind)
            },
            contesto
        )
    }
}

@Composable
fun EpisodeScreen(
    p: PaddingValues,
    scope: CoroutineScope,
    isAlive: Boolean
) {
    val contesto = LocalContext.current

    val imageLoader = ImageLoader.Builder(contesto)
        .components {
            add(
                if(android.os.Build.VERSION.SDK_INT >= 28)
                    ImageDecoderDecoder.Factory()
                else GifDecoder.Factory()
            )
        }.build()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Image(
            painter = painterResource(R.drawable.spyxfamily),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = 100.dp)
        )

        if(MainActivity.episodi == null || isAlive){
            if(!isAlive){
                Row (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(p),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ancora nulla qui...",
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 26.sp,
                        fontFamily = BitcountFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }else {
                AsyncImage(
                    model = ImageRequest.Builder(contesto)
                        .data(R.drawable.loading)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .width(300.dp)
                        .align(Alignment.TopCenter)
                        .padding(top = 170.dp)
                )
            }
        }else {
            Column (
                modifier = Modifier
                    .padding(p)
                    .padding(vertical = 15.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Episodi caricati:",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PressStart2PFontFamily
                )
                LazyColumn (
                    modifier = Modifier
                        .selectableGroup()
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val eps = MainActivity.episodi
                    itemsIndexed(MainActivity.episodi as List<String>){ index, episodio ->
                        EpisodeButton(
                            text = getSingleEp(episodio),
                            onClick = { TODO() }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun AvailableListScreen(p: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Image(
            painter = painterResource(R.drawable.blackbutler),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = 100.dp)
        )
    }
}



@Throws(IOException::class, ExecutionException::class, InterruptedException::class)
fun loadEpisodeList(
    selected: Int,
    epScelto: Int,
    contesto: Context,
    onSearching:(Boolean) -> Unit,
    scope: CoroutineScope,
    onEpFind:(Int) -> Unit
) {
    var selezionato = epScelto
    if (epScelto > 0 && epScelto < MainActivity.nEpisodes[selected] * 10) if (epScelto - 50 > 0) selezionato =
        epScelto - 50

    MainActivity.startVals[selected] = selezionato

    scope.launch {
        withContext(Dispatchers.IO){
            onSearching(true)
            try {
                MainActivity.episodi = getEpisodeList(
                    MainActivity.anime[selected],
                    MainActivity.nEpisodes[selected],
                    MainActivity.abslouteITA[selected],
                    MainActivity.startVals[selected]
                )
            } catch (e: IOException) {
                Toast.makeText(contesto, "1Errore nel caricamento degli episodi...", Toast.LENGTH_SHORT).show()
            } catch (e: ExecutionException) {
                Toast.makeText(contesto, "2Errore nel caricamento degli episodi...", Toast.LENGTH_SHORT).show()
            } catch (e: InterruptedException) {
                Toast.makeText(contesto, "3Errore nel caricamento degli episodi...", Toast.LENGTH_SHORT).show()
            }
            onSearching(false)
            val len = MainActivity.episodi?.size
            onEpFind(
                if(len != null)
                    len
                else 0
            )
        }
    }
}
