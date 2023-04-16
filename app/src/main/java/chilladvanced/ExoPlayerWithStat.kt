package chilladvanced

import com.google.android.exoplayer2.ExoPlayer

class ExoPlayerWithStat(val exoPlayer: ExoPlayer) : ExoPlayer by exoPlayer {
    override fun prepare() {
        // TODO
        Logger.register(
            exoPlayer.currentMediaItem?.requestMetadata?.mediaUri?.host ?: "",
            NETWORK_METHODS.EXO_PLAYER
        )
        exoPlayer.prepare()
    }


}