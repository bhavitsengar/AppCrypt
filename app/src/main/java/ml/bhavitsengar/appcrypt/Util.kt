package ml.bhavitsengar.appcrypt

import android.app.ActivityManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.SharedPreferences
import java.util.*

class Util {

    companion object {

        var lastUnlockedApp : String = ""

        fun getSharedPreferences(context: Context) : SharedPreferences{

            return context.getSharedPreferences(context.getString(R.string.shared_preference_key),Context.MODE_PRIVATE)

        }

        fun getForegroundApp(context: Context): String {
            var currentApp = "NULL"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val time = System.currentTimeMillis()
                val appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
                if (appList != null && appList.size > 0) {
                    val mySortedMap = TreeMap<Long, UsageStats>()
                    for (usageStats in appList) {
                        mySortedMap[usageStats.lastTimeUsed] = usageStats
                    }
                    if (!mySortedMap.isEmpty()) {
                        currentApp = mySortedMap[mySortedMap.lastKey()]!!.packageName
                    }
                }
            } else {
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val tasks = am.runningAppProcesses
                currentApp = tasks[0].processName
            }

            return currentApp
        }
    }

}