package ml.bhavitsengar.appcrypt.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ml.bhavitsengar.appcrypt.ApplockService

class ServiceRestarterBroadcastReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d(ServiceRestarterBroadcastReceiver::class.java.simpleName, "Service Stops! Oooooooooooooppppssssss!!!!")
        context?.startService(Intent(context, ApplockService::class.java))

    }

}