package com.me.animedownloader

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong


class Download(
    private var episodeUri: String,
    private var filePath: DocumentFile,
    private var contesto: Context,
    private var fileName: String
) {

    @Throws(IOException::class)
    fun scarica() {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .build()

        val opStream = creaOutputStream(this.contesto, this.filePath, this.fileName, "video/mp4")
            ?: throw okio.IOException("Impossibile creare un file sul dispositivo")
        val downloadedBytes = AtomicLong(0)
        val downloadComplete = CompletableFuture<Void?>()

        val request: Request = Request.Builder()
            .url(this.episodeUri)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                System.err.println("\nErrore durante il download: " + e.message)
                try {
                    opStream?.close()
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
                            val buffer = ByteArray(64 * 1024) //64 kB alla volta
                            var bytesRead: Int

                            while ((input.read(buffer).also { bytesRead = it }) != -1) {
                                opStream?.write(buffer, 0, bytesRead)
                                downloadedBytes.addAndGet(bytesRead.toLong())
                                // System.out.print("\rScaricati: " + downloadedBytes.get() + " bytes");
                            }

                            opStream?.close()
                            downloadComplete.complete(null)
                        }
                    }
                } catch (e: IOException) {
                    onFailure(call, e)
                } finally {
                    opStream.close()
                }
            }
        })

        try {
            downloadComplete.join() // blocca finché non completa
        } finally {
            println("Risorse chiuse correttamente.")
        }
    }
}