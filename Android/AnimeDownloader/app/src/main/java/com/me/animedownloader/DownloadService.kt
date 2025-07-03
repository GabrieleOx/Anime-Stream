package com.me.animedownloader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.documentfile.provider.DocumentFile
import com.me.animedownloader.MainActivity.Companion.animeSceltoFolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadService : Service() {

    companion object {
        private val scope = CoroutineScope(Dispatchers.IO)
        private var notificationCounter = 1000  // per notifiche multiple non-foreground
    }

    private val CHANNEL_ID = "DownloadServiceChannel"
    private val FOREGROUND_NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundWithNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("download_url")
        val fileName = intent?.getStringExtra("nome_episodio")
        if (url == null || fileName == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        val fatherFolder = animeSceltoFolder
        if (fatherFolder == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        // Notifica separata per il singolo download (non foreground)
        val downloadNotificationId = notificationCounter++
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Download in corso")
            .setContentText("Scaricando: $fileName")
            .setProgress(0, 0, true)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .build()

        // Mostra la notifica non-foreground per il download attivo
        notificationManager.notify(downloadNotificationId, notification)

        scope.launch {
            doDownload(url, fatherFolder, applicationContext, fileName)
            // Quando finito, rimuovi la notifica download specifica
            notificationManager.cancel(downloadNotificationId)

            stopSelf(startId)
        }

        return START_STICKY
    }

    private fun startForegroundWithNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Download Service")
            .setContentText("Servizio di download attivo")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setOngoing(true)  // notifiche foreground non cancellabili dall'utente
            .build()

        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun doDownload(url: String, folder: DocumentFile, cont: Context, name: String) {
        val d = Download(url, folder, cont, name)
        d.scarica()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Canale download",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }
}
