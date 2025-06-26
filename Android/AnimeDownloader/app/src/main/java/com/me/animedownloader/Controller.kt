package com.me.animedownloader

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.util.Scanner
import java.util.concurrent.ExecutionException
import kotlin.system.exitProcess


class Controller {
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
        private var findEpisodes: Thread = Thread()
    }

    fun setTree(specific: File, selected: Int) {
        for (f in specific.list()) {
        }
        //Codice per la visione ad albero
    }

    @Throws(IOException::class, ExecutionException::class, InterruptedException::class)
    fun loadEpisodeList(
        selected: Int,
        downloadThreads: ArrayList<Thread>,
        stopThreads: ArrayList<Thread>,
        contesto: Context
    ) {
        //setTree(specific, selected)

        val starter = intArrayOf(startVals[selected])

        println("Inserisci l'episodio ricercato:")
        val result = Scanner(System.`in`).nextLine()

        val epScelto = result.trim().toInt()
        if (epScelto > 0 && epScelto < nEpisodes[selected] * 10) if (epScelto - 50 > 0) starter[0] =
            epScelto - 50

        startVals[selected] = starter[0]

        findEpisodes = Thread {
            try {
                //getEpisodesToPalce(selected, downloadThreads, stopThreads, specific)
            } catch (e: IOException) {
                System.err.println("Errore nel caricamento degli episodi...")
            } catch (e: ExecutionException) {
                System.err.println("Errore nel caricamento degli episodi...")
            } catch (e: InterruptedException) {
                System.err.println("Errore nel caricamento degli episodi...")
            }
        }
        findEpisodes.start()
    }

    @Throws(IOException::class)
    fun loadAnimeList(
        downloadThreads: ArrayList<Thread>,
        stopThreads: ArrayList<Thread>,
        contesto: Context,
        attivita: AppCompatActivity
    ) {
        AnimeFinder.getAnime(anime, nEpisodes, abslouteITA, startVals, attivita, contesto)

        for (i in anime.indices) {
            val index = i
            //r.setOnAction(e -> {
            try {
                if (findEpisodes.isAlive) {
                } else {
                    //lastPressed = r;
                    loadEpisodeList(index, downloadThreads, stopThreads, contesto)
                }
            } catch (e1: IOException) {
                e1.printStackTrace()
            } catch (e1: ExecutionException) {
                e1.printStackTrace()
            } catch (e1: InterruptedException) {
                e1.printStackTrace()
            }
            //});
        }
    }

    @Throws(IOException::class, ExecutionException::class, InterruptedException::class)
    private fun getEpisodesToPalce(
        scelto: Int,
        downloadThreads: ArrayList<Thread>,
        stopThreads: ArrayList<Thread>,
        specific: File
    ): ArrayList<String> {
        episodi = getEpisodeList(
            scelto,
            anime[scelto], nEpisodes[scelto], abslouteITA[scelto], startVals[scelto]
        )
        val eps = ArrayList<String>()

        for (i in episodi.indices) {
            eps.add(AnimeDownloader.getSingleEp(episodi[i]))
            val index: Int = i + 1
            /*b.setOnAction(e -> { -->> Azione per ogni bottone di avviare un download
                AnimeDownloader.addDownload(downloadThreads, stopThreads, episodi, specific, index, slash, this, scelto);
            });*/
        }
        return eps
    }
}