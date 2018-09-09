package ml.bhavitsengar.appcrypt

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import ml.bhavitsengar.appcrypt.UI.RevealAnimation
import android.support.v4.app.ActivityCompat
import android.view.View
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import ml.bhavitsengar.appcrypt.broadcastreceiver.ServiceRestarterBroadcastReceiver


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class ApplockService : Service(){

    private val NOTIFICATION_ID = 999
    private var lastForegroundApp: String = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(ApplockService::class.java.simpleName, "onDestroy!")
        val broadcastIntent = Intent(ServiceRestarterBroadcastReceiver::class.java.name)
        sendBroadcast(broadcastIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Thread {

            while (true) {

                val appPackage = Util.getForegroundApp(this)

                val preferences = Util.getSharedPreferences(this)
                val prefMap = preferences.all

                val notificationBuilder = Notification.Builder(this)
                        .setContentTitle("App Crypt is active")
                        .setContentText("Configure this notification in App Crypt settings.")
                        .setSmallIcon(R.drawable.ic_launcher)

                // create the pending intent and add to the notification
                val intent = Intent(this, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
                notificationBuilder.setContentIntent(pendingIntent)

                // send the notification
                //NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notificationBuilder.build())

                startForeground(NOTIFICATION_ID,notificationBuilder.build());

                /**
                 * Here we are checking whether the current foreground app is present in the
                 * locked apps list. Then, the second condition we're checking that whether the user has navigated from the
                 * app to somewhere else or not, which is being done by monitoring {lastForegroundApp}, and we're also checking
                 * if the current foreground app is unlocked or not, using {Util.lastUnlockedApp}, which is used to handle conditions
                 * where an app has splash screens, which vanishes in seconds.
                 *
                 */
                if (prefMap.keys.contains(appPackage) &&
                        !(appPackage.equals(lastForegroundApp, true)
                                && appPackage.equals(Util.lastUnlockedApp, true))) {

                    val pinActivityIntent = Intent(this, CustomPinActivity::class.java)
                    pinActivityIntent.putExtra("foregroundApp", appPackage)
                    pinActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    startActivity(pinActivityIntent)

                }

                if (!appPackage.equals(packageName, true)) {
                    lastForegroundApp = appPackage
                }

                Thread.sleep(800)
            }

        }.start()

        return START_STICKY
    }

//    override fun onHandleIntent(intent: Intent?) {
//
//            while (true) {
//
//                val appPackage = Util.getForegroundApp(this)
//
//                val preferences = Util.getSharedPreferences(this)
//                val prefMap = preferences.all
//
//                for (lockedPackage in prefMap.keys) {
//
//
//                    /**
//                     * Here we are checking whether the current foreground app is present in the
//                     * locked apps list. Then, the second condition we're checking that whether the user has navigated from the
//                     * app to somewhere else or not, which is being done by monitoring {lastForegroundApp}, and we're also checking
//                     * if the current foreground app is unlocked or not, using {Util.lastUnlockedApp}, which is used to handle conditions
//                     * where an app has splash screens, which vanishes in seconds.
//                     *
//                     */
//                    if (lockedPackage.equals(appPackage, true) &&
//                            !(appPackage.equals(lastForegroundApp, true)
//                                    && appPackage.equals(Util.lastUnlockedApp, true))) {
//
//                        val pinActivityIntent = Intent(this, CustomPinActivity::class.java)
//                        pinActivityIntent.putExtra("foregroundApp", appPackage)
//                        startActivity(pinActivityIntent)
//
//                    }
//
//                }
//
//                if (!appPackage.equals(packageName, true)) {
//                    lastForegroundApp = appPackage
//                }
//
//                Thread.sleep(800)
//            }
//    }

    private fun startRevealActivity(v: View, appPackage: String) {
        //calculates the center of the View v you are passing
        val revealX = (v.x + v.width / 2)
        val revealY = (v.y + v.height / 2)

        //create an intent, that launches the second activity and pass the x and y coordinates
        val intent = Intent(this, CustomPinActivity::class.java)
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, revealX)
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, revealY)

        val pinActivityIntent = Intent(this, CustomPinActivity::class.java)
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, revealX)
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, revealY)
        pinActivityIntent.putExtra("foregroundApp", appPackage)
        startActivity(pinActivityIntent)


        //just start the activity as an shared transition, but set the options bundle to null
        ActivityCompat.startActivity(this, intent, null)

    }

}

