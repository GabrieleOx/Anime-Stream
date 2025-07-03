package com.me.animedownloader

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.analytics.AnalyticsListener
import io.sanghun.compose.video.RepeatMode
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.controller.VideoPlayerControllerConfig
import io.sanghun.compose.video.uri.VideoPlayerMediaItem

@Composable
fun NewVideoPlayer(
    video: Uri,
    goFullScreen:(Boolean) -> Unit
) {
    VideoPlayer(
        mediaItems = listOf(
            VideoPlayerMediaItem.StorageMediaItem(
                storageUri = video
            )
        ),
        handleLifecycle = true,
        autoPlay = true,
        usePlayerController = true,
        enablePip = false,
        handleAudioFocus = true,
        controllerConfig = VideoPlayerControllerConfig(
            showSpeedAndPitchOverlay = false,
            showSubtitleButton = false,
            showCurrentTimeAndTotalTime = true,
            showBufferingProgress = false,
            showForwardIncrementButton = true,
            showBackwardIncrementButton = true,
            showBackTrackButton = true,
            showNextTrackButton = true,
            showRepeatModeButton = true,
            controllerShowTimeMilliSeconds = 5_000,
            controllerAutoShow = true,
            showFullScreenButton = true
        ),
        volume = 0.5f,
        repeatMode = RepeatMode.NONE,
        onCurrentTimeChanged = {
            Log.e("CurrentTime", it.toString())
        },
        playerInstance = {
            addAnalyticsListener(object : AnalyticsListener {})
        },
        modifier = Modifier
            .fillMaxSize(),
        onFullScreenExit = {
            goFullScreen(false)
        },
        onFullScreenEnter = {
            goFullScreen(false)
        }
    )
}
