package ml.bhavitsengar.appcrypt

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.github.omadahealth.lollipin.lib.managers.AppLock
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity
import ml.bhavitsengar.appcrypt.UI.RevealAnimation
import android.widget.LinearLayout
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.support.transition.Explode
import android.support.transition.Transition
import android.view.Window
import android.view.WindowManager


class CustomPinActivity : AppLockActivity(){

    var mRevealAnimation: RevealAnimation? = null
    var isMainApp : Boolean = false
    var foregroundApp : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        foregroundApp = intent.getStringExtra("foregroundApp")

        isMainApp = intent.getBooleanExtra("isMainApp", false)

        val intent = this.intent   //get the intent to recieve the x and y coords, that you passed before

//        val rootLayout = findViewById<View>(android.R.id.content) //there you have to get the root layout of your second activity
//        mRevealAnimation = RevealAnimation(rootLayout, intent, this)

        try {

            val icon = packageManager.getApplicationIcon(foregroundApp)
            setLogo(icon)

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    override fun onPinSuccess(attempts: Int) {
        if(foregroundApp != null)
            Util.lastUnlockedApp = foregroundApp as String

        finish()
        //mRevealAnimation!!.unRevealActivity()

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

    override fun onPause() {
        super.onPause()
        if (Util.getSharedPreferences(this).getBoolean("isPinSetup", false))
            finish()

    }

    override fun getContentView(): Int {
        return R.layout.activity_custompin
    }

    private fun showHomeScreen() : Boolean{
        val startMain = Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(startMain)
        if(Util.getSharedPreferences(this).getBoolean("isPinSetup", false))
            finish()
        return true
    }
}