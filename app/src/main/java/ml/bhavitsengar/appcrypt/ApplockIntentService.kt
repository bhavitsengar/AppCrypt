package ml.bhavitsengar.appcrypt

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.app.ActivityManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import java.util.*
import kotlin.collections.HashMap


// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_FOO = "ml.bhavitsengar.appcrypt.action.FOO"
private const val ACTION_BAZ = "ml.bhavitsengar.appcrypt.action.BAZ"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "ml.bhavitsengar.appcrypt.extra.PARAM1"
private const val EXTRA_PARAM2 = "ml.bhavitsengar.appcrypt.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class ApplockIntentService : IntentService("ApplockIntentService") {

    var lastForegroundApp : String = ""

    override fun onHandleIntent(intent: Intent?) {
        while (true){
            val appPackage = getForegroundApp()

            val preferences = Util.getSharedPreferences(this)
            val prefMap = preferences.all

            for(lockedPackage in prefMap.keys){

                if(lockedPackage.equals(appPackage, true) && !appPackage.equals(lastForegroundApp, true)){
                    val intent = Intent(this, CustomPinActivity::class.java)
                    startActivity(intent)
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
            if (appList != null && appList.size > 0) {
                val mySortedMap = TreeMap<Long, UsageStats>()
                for (usageStats in appList) {
                    mySortedMap[usageStats.lastTimeUsed] = usageStats
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
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

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(param1: String, param2: String) {
        TODO("Handle action Foo")
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: String) {
        TODO("Handle action Baz")
    }

    companion object {

        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionFoo(context: Context, param1: String, param2: String) {
            val intent = Intent(context, ApplockIntentService::class.java).apply {
                action = ACTION_FOO
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, ApplockIntentService::class.java).apply {
                action = ACTION_BAZ
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }

}
