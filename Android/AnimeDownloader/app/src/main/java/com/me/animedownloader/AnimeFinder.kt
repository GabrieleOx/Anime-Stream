package com.me.animedownloader

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.system.exitProcess
import androidx.core.net.toUri


class AnimeFinder {
    companion object{
        @JvmStatic
        @Throws(IOException::class)
        fun getAnime(
            anime: ArrayList<String>,
            nEpisodes: ArrayList<Int>,
            abslouteITA: ArrayList<Boolean>,
            startValues: ArrayList<Int>,
            activity: ComponentActivity,
            contesto: Context
        ){

            val prefs = contesto.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val uriString = prefs.getString("persisted_uri", null)

            if(uriString != null){
                val uri = uriString.toUri()

                try {
                    val inputStream = contesto.contentResolver.openInputStream(uri)
                    val content = inputStream?.bufferedReader()?.use { it.readText() }

                    if (content != null) {
                        reader(content, anime, nEpisodes, abslouteITA, startValues)
                    }else {
                        val message = "Riferimenti agli anime mancanti\nCercali su GitHub..."
                        val duration = Toast.LENGTH_LONG
                        val errorMessage = Toast.makeText(contesto, message, duration)
                        errorMessage.show()
                        exitProcess(0)
                    }
                } catch (e: Exception){ e.printStackTrace() }
            }else {
                val filePicker = PickTxt(activity, activity.contentResolver, contesto)
                fun start(){
                    CoroutineScope(Dispatchers.Main).launch {
                        val content = filePicker.pickTextFile()

                        if (content != null) {
                            reader(content, anime, nEpisodes, abslouteITA, startValues)
                        }else {
                            val message = "Riferimenti agli anime mancanti\nCercali su GitHub..."
                            val duration = Toast.LENGTH_LONG
                            val errorMessage = Toast.makeText(contesto, message, duration)
                            errorMessage.show()
                            exitProcess(0)
                        }
                    }
                }

                start()
            }
        }

        @JvmStatic
        @Throws(IOException::class)
        private fun reader(
            x: String,
            arr: ArrayList<String>,
            nEps: ArrayList<Int>,
            absITA: ArrayList<Boolean>,
            starters: ArrayList<Int>
        ) {
            val str = StringBuilder()
            val eps = StringBuilder()
            val starterStr = StringBuilder()
            var ch: Int
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
        }
    }
}