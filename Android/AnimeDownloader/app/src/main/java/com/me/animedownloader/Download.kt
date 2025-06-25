package com.me.animedownloader

import org.asynchttpclient.AsyncCompletionHandler
import org.asynchttpclient.AsyncHandler
import org.asynchttpclient.AsyncHttpClientConfig
import org.asynchttpclient.Dsl
import org.asynchttpclient.HttpResponseBodyPart
import org.asynchttpclient.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.CompletableFuture
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
        val config: AsyncHttpClientConfig = Dsl.config()
            .setConnectTimeout(30000)
            .setReadTimeout(120000)
            .setRequestTimeout(600000)
            .build()

        val client = Dsl.asyncHttpClient(config)

        val opStream = FileOutputStream(
            getFile(
                fileUrl!!,
                filePath!!
            )
        )
        val downloadedBytes = AtomicLong(0)
        val downloadComplete = CompletableFuture<Void?>()

        client.prepareGet(this.fileUrl).execute<Void>(object : AsyncCompletionHandler<Void?>() {
            var totalBytes: Long = -1

            @Throws(Exception::class)
            override fun onBodyPartReceived(bodyPart: HttpResponseBodyPart): AsyncHandler.State {
                opStream.channel.write(bodyPart.bodyByteBuffer)
                val current = downloadedBytes.addAndGet(bodyPart.length().toLong())
                //System.out.print("\rScaricati: " + current + " bytes");
                return AsyncHandler.State.CONTINUE
            }

            @Throws(Exception::class)
            override fun onCompleted(response: Response): Void? {
                val contentLength = response.getHeader("Content-Length")
                if (contentLength != null) {
                    totalBytes = contentLength.toLong()
                }
                AnimeDownloader.downloadThredStop.set(indiceStop, true)
                opStream.close()
                downloadComplete.complete(null)
                return null
            }

            override fun onThrowable(t: Throwable) {
                System.err.println(
                    """
                    
                    Errore durante il download: ${t.message}
                    """.trimIndent()
                )
                AnimeDownloader.downloadThredStop.set(indiceStop, true)
                try {
                    opStream.close()
                } catch (ignored: IOException) {
                }
                downloadComplete.completeExceptionally(t)
            }
        })

        try {
            while (!AnimeDownloader.downloadThredStop.get(indiceStop));
        } finally {
            client.close() // CHIUSURA SICURA
        }

        println("Risorse chiuse correttamente.")
    }
}