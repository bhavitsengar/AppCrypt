package ml.bhavitsengar.appcrypt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val startServiceIntent = Intent(context, ApplockIntentService::class.java)
        context.startService(startServiceIntent)
    }
}
