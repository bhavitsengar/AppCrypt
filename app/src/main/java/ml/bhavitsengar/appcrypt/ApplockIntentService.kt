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

