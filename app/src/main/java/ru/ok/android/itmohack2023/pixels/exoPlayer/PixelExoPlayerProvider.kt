package ru.ok.android.itmohack2023.pixels.exoPlayer

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector
import com.google.android.exoplayer2.util.Clock

class PixelExoPlayerProvider {
    fun getClient(context: Context) : ExoPlayer {
        val analyticsCollector = DefaultAnalyticsCollector(Clock.DEFAULT)

        analyticsCollector.addListener(ExoplayerListener())

        return ExoPlayer.Builder(context).setAnalyticsCollector(analyticsCollector).build()
    }
}