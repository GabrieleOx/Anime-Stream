package com.me.animedownloader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class EpSelectionViewModel : ViewModel() {

    var isDialogShown by mutableStateOf(false)
        private set

    fun onAnimeChoose(){
        isDialogShown = true
    }

    fun onDismissDialog(){
        isDialogShown = false
    }
}