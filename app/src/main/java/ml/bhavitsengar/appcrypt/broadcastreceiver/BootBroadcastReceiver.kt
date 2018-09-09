package ml.bhavitsengar.appcrypt.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ml.bhavitsengar.appcrypt.ApplockService

class BootBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if ("android.intent.action.BOOT_COMPLETED" == intent.action ||
                "android.intent.action.QUICKBOOT_POWERON" == intent.action ||
                "com.htc.intent.action.QUICKBOOT_POWERON" == intent.action) {
            val startServiceIntent = Intent(context, ApplockService::class.java)
            context.startService(startServiceIntent)
        }

    }
}
