package chilladvanced

import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource


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