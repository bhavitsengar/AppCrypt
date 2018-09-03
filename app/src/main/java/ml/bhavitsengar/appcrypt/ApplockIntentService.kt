package ml.bhavitsengar.appcrypt

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.app.ActivityManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import java.util.*
import kotlin.collections.HashMap

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class ApplockIntentService : IntentService("ApplockIntentService") {

    private var lastForegroundApp : String = ""

    override fun onHandleIntent(intent: Intent?) {
        while (true){
            val appPackage = getForegroundApp()

            val preferences = Util.getSharedPreferences(this)
            val prefMap = preferences.all

            for(lockedPackage in prefMap.keys){

                if(lockedPackage.equals(appPackage, true) && !appPackage.equals(lastForegroundApp, true)){
                    val pinActivityIntent = Intent(this, CustomPinActivity::class.java)
                    startActivity(pinActivityIntent)
                }

            }

            if(!appPackage.equals(packageName, true)){
                lastForegroundApp = appPackage
            }

            Thread.sleep(1000)
        }
    }

    private fun getForegroundApp(): String {
        var currentApp = "NULL"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
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
            val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = am.runningAppProcesses
            currentApp = tasks[0].processName
        }

        return currentApp
    }


}
