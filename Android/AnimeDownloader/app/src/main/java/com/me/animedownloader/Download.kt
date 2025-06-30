package com.me.animedownloader

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong


class Download(private var fileUrl: String?, private var filePath: String?) {

    companion object{
        @JvmStatic
        private fun getFile(url: String, path: String): File {
            if (path.substring(path.length - 4, path.length) == ".mp4") return File(path)

            val fileName = StringBuilder()
            var posSlash = url.length - 1
            while (true) {
                if (url[posSlash] != '/') fileName.append(url[posSlash])
                else break
                posSlash--
            }
            fileName.reverse()

            return File(path + fileName)
        }
    }

    @Throws(IOException::class)
    fun scarica(indiceStop: Int) {
        // 1. Configura OkHttpClient con timeout equivalenti
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .build()

        // 2. Prepara stream e variabili
        val opStream = FileOutputStream(getFile(this.fileUrl!!, this.filePath!!))
        val downloadedBytes = AtomicLong(0)
        val downloadComplete = CompletableFuture<Void?>()

        // 3. Costruisci richiesta GET
        val request: Request = Request.Builder()
            .url(this.fileUrl!!)
            .build()

        // 4. Avvia download asincrono
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                System.err.println("\nErrore durante il download: " + e.message)
                //AnimeDownloader.downloadThredStop.set(indiceStop, true)
                try {
                    opStream.close()
                } catch (ignored: IOException) {
                }
                downloadComplete.completeExceptionally(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onFailure(call, IOException("Risposta non valida: " + response))
                    return
                }

                var totalBytes: Long = -1
                val clHeader = response.header("Content-Length")
                if (clHeader != null) {
                    totalBytes = clHeader.toLong()
                }

                try {
                    response.body.use { body ->
                        body.byteStream().use { input ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int

                            while ((input.read(buffer).also { bytesRead = it }) != -1) {
                                opStream.write(buffer, 0, bytesRead)
                                downloadedBytes.addAndGet(bytesRead.toLong())
                                // System.out.print("\rScaricati: " + downloadedBytes.get() + " bytes");
                            }

                            //AnimeDownloader.downloadThredStop.set(indiceStop, true)
                            opStream.close()
                            downloadComplete.complete(null)
                        }
                    }
                } catch (e: IOException) {
                    onFailure(call, e)
                }
            }
        })

        // 5. Attesa del completamento in modo "bloccante" (come il while del tuo codice)
        try {
            downloadComplete.join() // blocca finché non completa
        } finally {
            println("Risorse chiuse correttamente.")
        }
    }
}