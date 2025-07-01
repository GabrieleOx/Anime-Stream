package com.me.animedownloader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadService : Service() {

    companion object {
        private var notId = 1
        private val scope = CoroutineScope(Dispatchers.IO)
    }

    private val CHANNEL_ID = "DownloadServiceChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("download_url")
        if (url == null) {
            stopSelf(startId) // Nessun URL, stoppa il servizio
            return START_NOT_STICKY
        }

        val currentNotificationId = notId++
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Download in corso")
            .setContentText("Scaricando: $url")
            .setSmallIcon(R.drawable.icona) // metti la tua icona
            .build()

        // Avvia il servizio in foreground con notifica unica per questo download
        startForeground(currentNotificationId, notification)

        scope.launch {
            doDownload(url)
            stopSelf(startId)  // Ferma il servizio per questo startId quando finito
        }

        return START_STICKY
    }

    private suspend fun doDownload(url: String) {
        val d = Download(url, "Path")
        d.scarica()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Canale download",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
