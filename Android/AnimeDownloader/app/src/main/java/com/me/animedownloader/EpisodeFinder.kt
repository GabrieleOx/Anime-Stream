package com.me.animedownloader

import org.asynchttpclient.Dsl
import org.asynchttpclient.Response
import java.io.IOException
import java.util.concurrent.ExecutionException


@Throws(IOException::class, ExecutionException::class, InterruptedException::class)
fun getEpisodeList(
    animeScelto: String,
    indiceEpisodi: Int,
    itaAssoluto: Boolean,
    start: Int
): ArrayList<String> {

    var i = start
    val episodes = ArrayList<String>()

    while (true) {
        val name = nameComposer(i, animeScelto, indiceEpisodi, itaAssoluto)
        if (!isPresent(name)) break
        else {
            episodes.add(name)
        }
        i++
        if (i > start + 100) break
    }

    return episodes
}

@Throws(IOException::class, InterruptedException::class, ExecutionException::class)
fun isPresent(url: String?): Boolean {
    val ret = BooleanArray(1)
    changeRet(ret, false)
    Dsl.asyncHttpClient().use { client ->
        client.prepareHead(url).execute().toCompletableFuture()
            .thenAccept { response: Response ->
                val statusCode = response.statusCode
                if (statusCode == 200) {
                    changeRet(ret, true)
                } else changeRet(ret, false)
            }.join() // Aspetta che finisca la richiesta async
    }
    return ret[0]
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