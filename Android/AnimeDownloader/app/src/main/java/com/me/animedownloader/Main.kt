package com.me.animedownloader

import java.io.IOException
import java.util.Scanner
import kotlin.system.exitProcess


@Throws(IOException::class)
fun main() {

}

fun onClose() {
    var downloadInCorso = 0
    var downloadTrovati: Int
    for (i in 0..<downloadThredStop.size) if (!downloadThredStop[i]
    ) downloadInCorso++

    if (downloadInCorso > 0) {
        val str =
            "Ci sono ancora $downloadInCorso download in corso:\nVuoi che l'app si riduca ad icona e termini i download in\nautonomia per poi chiudersi?"
        println(str)
        println("Inserisci 1 per confermare:")

        if (Scanner(System.`in`).nextInt() == 1) {
            downloadTrovati = downloadInCorso
            while (downloadTrovati > 0) {
                downloadTrovati = 0
                for (i in 0..<downloadThredStop.size) if (!downloadThredStop[i]
                ) downloadTrovati++
            }
            exitProcess(0)
        } else {
            exitProcess(0)
        }
    } else exitProcess(0)
}