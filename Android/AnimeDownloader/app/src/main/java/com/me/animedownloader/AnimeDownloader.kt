package com.me.animedownloader

import java.io.File
import java.io.IOException
import kotlin.concurrent.Volatile


class AnimeDownloader {
    companion object{
        @Volatile
        @JvmStatic
        var downloadThredStop: ArrayList<Boolean> = ArrayList()

        @JvmStatic
        fun getSingleEp(episodeUrl: String): String {
            var j = episodeUrl.length - 4
            while (episodeUrl[j - 1] != '/') j--
            return episodeUrl.substring(j, episodeUrl.length - 4)
        }

        @JvmStatic
        fun addDownload(
            start: ArrayList<Thread>,
            stop: ArrayList<Thread>,
            episodi: ArrayList<String?>,
            cartella: File,
            nEp: Int,
            slash: Char,
            selected: Int
        ) {
            downloadThredStop.add(false)
            start.add(Thread {
                val iStop = downloadThredStop.size - 1
                val d = Download(episodi[nEp - 1], cartella.absolutePath + slash)
                try {
                    d.scarica(iStop)
                } catch (e: IOException) {
                }
            })
            start[downloadThredStop.size - 1].start()
            stop.add(Thread {
                val iS = downloadThredStop.size - 1
                while (!downloadThredStop[iS]);
                start[iS].interrupt()
            })
            stop[downloadThredStop.size - 1].start()
        }
    }
}