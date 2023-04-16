package chilladvanced.com.google.android.exoplayer2

import chilladvanced.Logger
import chilladvanced.NETWORK_METHODS
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class ExoPlayerWithStat(val exoPlayer: ExoPlayer) : ExoPlayer by exoPlayer {
    override fun prepare() {
        exoPlayer.prepare()
        val mediaSource: MediaItem? = exoPlayer.currentMediaItem
        Logger.register(
            mediaSource?.requestMetadata?.mediaUri?.host ?: " ",
            NETWORK_METHODS.EXO_PLAYER
        )
    }


}