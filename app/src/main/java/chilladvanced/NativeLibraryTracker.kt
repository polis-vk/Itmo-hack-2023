package chilladvanced

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.TrafficStats
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.google.android.exoplayer2.util.Log


class NativeLibraryTracker() {
//    val currentThread: Int
//    val totalTxBytes = TrafficStats.getTotalTxBytes()
//    init {
//        currentThread = TrafficStats.getUidRxBytes()
//        TrafficStats.getThreadStatsTag()
//        val totalRxBytes = TrafficStats..getTotalRxBytes()
//    }

    var trafficStatsTx = 0L
    var started: Boolean = false
    var trafficStatsRx = 0L
    var uid = -1;

    fun start(context: Activity) {
        // May be we got permission for another task
        try {
            if (checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {


                uid = context.applicationInfo.uid
                TrafficStats.setThreadStatsTag(uid)
                trafficStatsRx = TrafficStats.getTotalRxBytes()
                trafficStatsTx = TrafficStats.getTotalTxBytes()

            }
        } catch (e: Exception) {
            Log.w(
                "NativeLibraryTracker",
                "Something went wrong in NativeLibraryTracker: " + e.message
            )
        }
    }

    fun stop(context: Context) {
        try {
            if (started && checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {
                TrafficStats.setThreadStatsTag(uid)
                Logger.log(
                    "", NETWORK_METHODS.NATIVE, 0,
                    TrafficStats.getTotalRxBytes() - trafficStatsRx,
                    TrafficStats.getTotalTxBytes() - trafficStatsTx
                )
            }
        } catch (e: Exception) {
            Log.w(
                "NativeLibraryTracker",
                "Something went wrong in NativeLibraryTracker: " + e.message
            )
        }
    }
}
