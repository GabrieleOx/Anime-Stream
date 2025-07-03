package com.me.animedownloader

import android.content.Context
import android.widget.Toast
import androidx.core.net.toUri
import java.io.IOException
import kotlin.system.exitProcess


class AnimeFinder {
    companion object{
        @JvmStatic
        @Throws(IOException::class)
        fun getAnime(
            nEpisodes: ArrayList<Int>,
            abslouteITA: ArrayList<Boolean>,
            startValues: ArrayList<Int>,
            contesto: Context,
            uriString: String
        ): ArrayList<String> {
            var retArr = ArrayList<String>()

            val uri = uriString.toUri()

            try {
                val inputStream = contesto.contentResolver.openInputStream(uri)
                val content = inputStream?.bufferedReader()?.use { it.readText() }

                if (content != null) {
                    retArr = reader(content, nEpisodes, abslouteITA, startValues)
                }else {
                    val message = "Riferimenti agli anime mancanti\nCercali su GitHub..."
                    val duration = Toast.LENGTH_LONG
                    val errorMessage = Toast.makeText(contesto, message, duration)
                    errorMessage.show()
                    exitProcess(0)
                }
            } catch (e: Exception){ e.printStackTrace() }

            return retArr
        }

        suspend fun pickAnimeFile(
            filePick: PickTxt,
            contesto: Context,
            nEpisodes: ArrayList<Int>,
            abslouteITA: ArrayList<Boolean>,
            startValues: ArrayList<Int>
        ): ArrayList<String> {
            var retArr: ArrayList<String>

            val content = filePick.pickTextFile()

            if (content != null) {
                retArr = reader(content, nEpisodes, abslouteITA, startValues)
            }else {
                val message = "Riferimenti agli anime mancanti\nCercali su GitHub..."
                val duration = Toast.LENGTH_LONG
                val errorMessage = Toast.makeText(contesto, message, duration)
                errorMessage.show()
                exitProcess(0)
            }

            return retArr
        }

        @JvmStatic
        @Throws(IOException::class)
        private fun reader(
            x: String,
            nEps: ArrayList<Int>,
            absITA: ArrayList<Boolean>,
            starters: ArrayList<Int>
        ): ArrayList<String> {
            val arr = ArrayList<String>()
            val str = StringBuilder()
            val eps = StringBuilder()
            val starterStr = StringBuilder()
            var spaces = 0
            var num = false
            var abs = false
            for(carattere in x) {
                if (carattere == '\n') {
                    if (starterStr.toString().trim().isNotEmpty()) {
                        if (starterStr.toString().trim().toInt() > 0) starters.add(
                            starterStr.toString().trim().toInt()
                        )
                        starters.add(1)
                    } else starters.add(1)

                    arr.add(str.toString())
                    nEps.add(eps.toString().trim().toInt())
                    absITA.add(abs)
                    str.delete(0, str.length)
                    eps.delete(0, eps.length)
                    starterStr.delete(0, starterStr.length)
                    num = false
                    abs = false
                    spaces = 0
                } else {
                    if (carattere == ' ') {
                        num = true
                        spaces++
                        continue
                    }

                    if (num && carattere == '@') {
                        abs = true

                        continue
                    }

                    if (spaces > 1 && (carattere in '0'..'9')) { // da sistemare per onePieceSubIta
                        starterStr.append(carattere)
                        continue
                    }

                    if (num) eps.append(carattere)
                    else str.append(carattere)
                }
            }
            arr.add(str.toString())
            nEps.add(eps.toString().trim().toInt())
            absITA.add(abs)

            if (starterStr.toString().trim().isNotEmpty()) {
                if (starterStr.toString().trim()
                        .toInt() > 0) starters.add(starterStr.toString().trim().toInt())
                starters.add(1)
            } else starters.add(1)

            return arr
        }
    }
}