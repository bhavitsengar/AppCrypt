package ml.bhavitsengar.appcrypt

import android.app.IntentService
import android.content.Intent


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class ApplockIntentService : IntentService("ApplockIntentService"){

    private var lastForegroundApp: String = ""

    override fun onHandleIntent(intent: Intent?) {

            while (true) {

                val appPackage = Util.getForegroundApp(this)

                val preferences = Util.getSharedPreferences(this)
                val prefMap = preferences.all

                for (lockedPackage in prefMap.keys) {


                    /**
                     * Here we are checking whether the current foreground app is present in the
                     * locked apps list. Then, the second condition we're checking that whether the user has navigated from the
                     * app to somewhere else or not, which is being done by monitoring {lastForegroundApp}, and we're also checking
                     * if the current foreground app is unlocked or not, using {Util.lastUnlockedApp}, which is used to handle conditions
                     * where an app has splash screens, which vanishes in seconds.
                     *
                     */
                    if (lockedPackage.equals(appPackage, true) &&
                            !(appPackage.equals(lastForegroundApp, true)
                                    && appPackage.equals(Util.lastUnlockedApp, true))) {

                        val pinActivityIntent = Intent(this, CustomPinActivity::class.java)
                        pinActivityIntent.putExtra("foregroundApp", appPackage)
                        startActivity(pinActivityIntent)

                    }

                }

                if (!appPackage.equals(packageName, true)) {
                    lastForegroundApp = appPackage
                }

                Thread.sleep(1000)
            }
    }

}

