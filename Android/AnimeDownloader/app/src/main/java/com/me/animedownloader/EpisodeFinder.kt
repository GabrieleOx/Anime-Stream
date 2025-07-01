package com.me.animedownloader

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.ExecutionException

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
    if (epScelto > 0 && epScelto < MainActivity.nEpisodes[selected] * 10) if (epScelto - 25 > 0) selezionato =
        epScelto - 25

    MainActivity.startVals[selected] = selezionato

    scope.launch {
        withContext(Dispatchers.IO){
            onSearching(true)
            onEpFind(0)
            try {
                MainActivity.episodi = getEpisodeList(
                    MainActivity.anime[selected],
                    MainActivity.nEpisodes[selected],
                    MainActivity.abslouteITA[selected],
                    MainActivity.startVals[selected]
                )
            } catch (e: IOException) {
                Toast.makeText(contesto, "Errore nel caricamento degli episodi...", Toast.LENGTH_SHORT).show()
            } catch (e: ExecutionException) {
                Toast.makeText(contesto, "Errore nel caricamento degli episodi...", Toast.LENGTH_SHORT).show()
            } catch (e: InterruptedException) {
                Toast.makeText(contesto, "Errore nel caricamento degli episodi...", Toast.LENGTH_SHORT).show()
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

@Throws(IOException::class, ExecutionException::class, InterruptedException::class)
fun getEpisodeList(
    animeScelto: String,
    indiceEpisodi: Int,
    itaAssoluto: Boolean,
    start: Int
): ArrayList<String> {
    val client = OkHttpClient()

    var i = if(start == 0) 1 else start
    val episodes = ArrayList<String>()

    while (true) {
        val name = nameComposer(i, animeScelto, indiceEpisodi, itaAssoluto)
        if (!isPresent(name, client)) break
        else {
            episodes.add(name)
        }
        i++
        if (i > start + 50) break
    }

    return episodes
}

@Throws(IOException::class, InterruptedException::class, ExecutionException::class)
fun isPresent(url: String, client: OkHttpClient): Boolean {
    var ret = false

    val call: Call = client.newCall(Request.Builder().url(url).build()) //è una richiesta sincrona.....
    call.execute().use { res ->
        val statusCode: Int = res.code
        ret = statusCode == 200
    }
    return ret
}

private fun changeRet(arr: BooleanArray, `val`: Boolean) {
    arr[0] = `val`
}

private fun nameComposer(
    epNumber: Int,
    baseUrl: String,
    totEpisodi: Int,
    absITA: Boolean
): String {
    val nEpisode = StringBuilder()
    nEpisode.append("_Ep_")
    nEpisode.append(numeroEpisodio(epNumber, totEpisodi))

    val serie = getAnimeName(baseUrl)
    val ep: String
    val ita = serie.substring(serie.length - 3, serie.length) == "ITA"
    val subIta = serie.substring(serie.length - 6, serie.length) == "SUBITA"

    if (absITA) {
        nEpisode.append("_ITA")
        ep = "$serie$nEpisode.mp4"
    } else if (ita && !subIta) {
        nEpisode.append("_ITA")
        ep = serie.substring(0, serie.length - 3) + nEpisode + ".mp4"
    } else if (subIta) {
        nEpisode.append("_SUB_ITA")
        ep = serie.substring(0, serie.length - 6) + nEpisode + ".mp4"
    } else {
        nEpisode.append("_SUB_ITA")
        ep = "$serie$nEpisode.mp4"
    }

    return baseUrl + ep
}

private fun numeroEpisodio(n: Int, nEpisodes: Int): String {
    var nEpisodes = nEpisodes
    var nZeroes = 0
    var nCiphers = 0
    var nCopy = n
    while (nEpisodes > 9) {
        nZeroes++
        nEpisodes /= 10
    }
    while (nCopy > 9) {
        nCiphers++
        nCopy /= 10
    }
    val num = StringBuilder()
    for (i in 0..<(nZeroes - nCiphers)) num.append(0)
    num.append(n)
    return num.toString()
}

fun getAnimeName(url: String): String {
    var slash = 0
    val urlLen = url.length
    var j = urlLen
    while (slash < 2) {
        j--
        if (url[j] == '/') slash++
    }
    return url.substring(j + 1, urlLen - 1)
}