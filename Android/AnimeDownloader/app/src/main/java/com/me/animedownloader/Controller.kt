package com.me.animedownloader

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.util.Scanner
import java.util.concurrent.ExecutionException
import kotlin.system.exitProcess


class Controller {

    fun setTree(specific: File, selected: Int) {
        for (f in specific.list()) {
        }
        //Codice per la visione ad albero
    }

    /*@Throws(IOException::class, ExecutionException::class, InterruptedException::class)
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
        attivita: ComponentActivity
    ): ArrayList<String> {
        AnimeFinder.getAnime(anime, nEpisodes, abslouteITA, startVals, attivita, contesto)
        return anime
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
    }*/
}