package com.me.animedownloader

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import androidx.core.net.toUri
import androidx.core.content.edit

class PickDirectory(
    caller: ActivityResultCaller,
    private val context: Context
) {
    private lateinit var continuation: (Result<Uri?>) -> Unit

    private val launcher: ActivityResultLauncher<Uri?> =
        caller.registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
            if (::continuation.isInitialized) {
                if (uri != null) {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    persistFolderUri(uri)
                    continuation(Result.success(uri))
                } else {
                    continuation(Result.success(null)) // Utente ha annullato
                }
            }
        }

    // 🔹 Forza la scelta della cartella
    suspend fun pickFolderAndPersist(): Uri? = suspendCancellableCoroutine { cont ->
        continuation = { result ->
            cont.resume(result.getOrNull())
        }
        launcher.launch(null)
    }

    // 🔹 Restituisce l’URI salvato (se esiste)
    fun getPersistedFolderUri(): Uri? {
        val prefs = context.getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)
        val uriString = prefs.getString("persisted_folder_uri", null)
        return uriString?.toUri()
    }

    private fun persistFolderUri(uri: Uri) {
        val prefs = context.getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)
        prefs.edit { putString("persisted_folder_uri", uri.toString()) }
    }
}
