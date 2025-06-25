package com.me.animedownloader

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PickTxt(
    caller: ActivityResultCaller, // può essere Activity o Fragment
    private val contentResolver: android.content.ContentResolver,
    contesto: Context
) {
    private lateinit var continuation: (Result<String?>) -> Unit

    private val launcher: ActivityResultLauncher<Array<String>> =
        caller.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (::continuation.isInitialized) {
                if (uri != null) {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    val prefs = contesto.getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)
                    prefs.edit().putString("persisted_uri", uri.toString()).apply()

                    val content = readTextFromUri(uri)
                    continuation(Result.success(content))
                } else {
                    continuation(Result.success(null)) // l’utente ha annullato
                }
            }
        }

    suspend fun pickTextFile(): String? = suspendCancellableCoroutine { cont ->
        continuation = { result ->
            cont.resume(result.getOrNull())
        }
        launcher.launch(arrayOf("text/plain"))
    }

    private fun readTextFromUri(uri: Uri): String {
        return contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
    }
}


