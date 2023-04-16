package ru.ok.android.itmohack2023.pixels.exoPlayer

import android.util.Log
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import ru.ok.android.itmohack2023.db.DbManager
import ru.ok.android.itmohack2023.db.DbModel
import ru.ok.android.itmohack2023.db.DbSingletone
import ru.ok.android.itmohack2023.pixels.MeasurementService

public class ExoplayerListener : AnalyticsListener {
    override fun onLoadStarted(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData
    ) {
        Log.d("Exoplayer Request", "EXO Player Start")

        MeasurementService.start(loadEventInfo.loadTaskId.toString())
    }

    override fun onLoadCompleted(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData
    ) {
        Log.d("Exoplayer Request", "Url: ${loadEventInfo.uri.toString()}")
        Log.d("Exoplayer Request", "Size: ${loadEventInfo.bytesLoaded}")

        val exception = Exception()
        val stackTrace = exception.stackTrace

        val time = MeasurementService.end(loadEventInfo.loadTaskId.toString())

        val modelInstance = DbModel(
            userID = DbSingletone.userId,
            time = time.toString(),
            client = "ExoPlayer",
            path = loadEventInfo.uri.toString(),
            responseSize = loadEventInfo.bytesLoaded.toString(),
            className = stackTrace[4].className,
            methodName = stackTrace[4].methodName,
        )

        val tmpDb = DbManager (DbSingletone.context)
        tmpDb.openDb()
        tmpDb.insertToDb(modelInstance)
    }
}