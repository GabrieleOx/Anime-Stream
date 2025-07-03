package com.me.animedownloader

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.me.animedownloader.MainActivity.Companion.abslouteITA
import com.me.animedownloader.MainActivity.Companion.anime
import com.me.animedownloader.MainActivity.Companion.animeSceltoFolder
import com.me.animedownloader.MainActivity.Companion.nEpisodes
import com.me.animedownloader.MainActivity.Companion.saveDirectory
import com.me.animedownloader.MainActivity.Companion.startVals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

        if(anime.isNotEmpty()){
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
                                    enabled = saveDirectory != null,
                                    selected = currentName == selectedAnime,
                                    onClick = {
                                        animeSceltoFolder = creaSeNonEsiste(saveDirectory!!, getAnimeName(currentName))

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
        }else {
            Row (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(p),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Nessun anime caricato...\n\n(Vai nelle impo. per caricarli)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 150.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp,
                    fontFamily = BitcountFontFamily,
                    fontWeight = FontWeight.Bold
                )
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
                        text = "Ancora nulla qui...\n\n (Ricorda di scegliere dove effettuare i download nelle impo.)",
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
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(MainActivity.episodi as List<String>){ index, episodio ->
                        EpisodeButton(
                            text = getSingleEp(episodio),
                            onClick = {
                                val intent = Intent(contesto, DownloadService::class.java).apply {
                                    putExtra("download_url", episodio)
                                    putExtra("nome_episodio", getSingleEp(episodio))
                                }
                                ContextCompat.startForegroundService(contesto, intent)
                            },
                            enabled = saveDirectory != null
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
            painter = painterResource(R.drawable.anya),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = 100.dp)
        )

        if(saveDirectory != null){
            Column (
                modifier = Modifier
                    .padding(p)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Episodi disponibii:",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PressStart2PFontFamily
                )
                LazyColumn {
                    items(saveDirectory!!.listFiles()){ file ->
                        var showEps by remember { mutableStateOf(false) }

                        if(file.isDirectory){
                            Column {
                                FolderButton(
                                    text = file.name!!,
                                    onClick = { showEps = !showEps }
                                )
                                if(showEps){
                                    for (video in file.listFiles()){
                                        if(!video.isDirectory && video != null){
                                            Row (
                                                modifier = Modifier
                                                    .padding(start = 30.dp)
                                            ){
                                                VideoButton(
                                                    text = video.name!!,
                                                    onClick = { TODO() }
                                                )
                                                DeleteButton(
                                                    onClick = {
                                                        showEps = false
                                                        video.delete()
                                                        showEps = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    p: PaddingValues,
    txtPicker: PickTxt,
    dirPicker: PickDirectory
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Image(
            painter = painterResource(R.drawable.gojo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = 100.dp)
        )

        Column (
            modifier = Modifier
                .padding(p)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(100.dp)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 60.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Scegli il file contenente gli anime!\n(Lo trovi su github...)",
                    color = Color(39, 177, 229, 255),
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = {
                        scope.launch {
                            anime = AnimeFinder.pickAnimeFile(txtPicker, context, nEpisodes, abslouteITA, startVals)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonColors(
                        Color.Blue,
                        Color.White,
                        Color.Gray,
                        Color.LightGray
                    )
                ) {
                    Text( text = "Scegli" )
                }
            }

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Scegli dove salvare gli episodi scaricati!",
                    color = Color(229, 39, 39, 255),
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = {
                        scope.launch {
                            val folderuri = dirPicker.pickFolderAndPersist()
                            if(folderuri != null){
                                saveDirectory = creaSeNonEsiste(context, folderuri, "AnimeDownload")
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonColors(
                        Color.Red,
                        Color.White,
                        Color.Gray,
                        Color.LightGray
                    )
                ) {
                    Text( text = "Scegli" )
                }
            }
        }
    }
}