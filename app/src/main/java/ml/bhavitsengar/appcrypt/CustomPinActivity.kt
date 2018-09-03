package ml.bhavitsengar.appcrypt

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import com.github.omadahealth.lollipin.lib.managers.AppLock
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity

class CustomPinActivity : AppLockActivity(){

    var isMainApp : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isMainApp = intent.getBooleanExtra("isMainApp", false)
    }

    override fun onPinSuccess(attempts: Int) {

        finish()
        if(isMainApp)
            Util.getSharedPreferences(this).edit().putBoolean("isAppUnlocked", true).apply()
    }

    override fun onPinFailure(attempts: Int) {
        Toast.makeText(this, "Wrong Pin", Toast.LENGTH_SHORT).show()
    }

    override fun showForgotDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBackPressed() {
        showHomeScreen()
    }

    private fun showHomeScreen() : Boolean{
        val startMain = Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(startMain);
        return true
    }
}