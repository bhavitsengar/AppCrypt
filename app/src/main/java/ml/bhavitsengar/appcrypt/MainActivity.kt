package ml.bhavitsengar.appcrypt

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ml.bhavitsengar.appcrypt.fragment.AllAppsFragment
import ml.bhavitsengar.appcrypt.model.AppInfo
import android.content.Context
import android.content.Intent
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import ml.bhavitsengar.appcrypt.fragment.LockedAppsFragment
import ml.bhavitsengar.appcrypt.fragment.UnlockedAppsFragment
import java.util.*
import com.github.omadahealth.lollipin.lib.managers.LockManager
import com.github.omadahealth.lollipin.lib.managers.AppLock
import android.content.pm.PackageManager
import android.app.AppOpsManager
import android.app.Notification
import android.app.PendingIntent
import android.content.DialogInterface
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.os.Build
import android.support.v4.app.NotificationManagerCompat


class MainActivity : AppCompatActivity(), AllAppsFragment.OnListFragmentInteractionListener
        , LockedAppsFragment.OnListFragmentInteractionListener, UnlockedAppsFragment.OnListFragmentInteractionListener{

    private val NOTIFICATION_ID = 999
    private val REQUEST_CODE_ENABLE = 99
    var usageAccPermDialog : AlertDialog? = null
    var toolbar : Toolbar? = null

    // Used to hold all the apps of system.
    private var appList : ArrayList<AppInfo> = ArrayList()

    // Used to find the index of the app in appList corresponding to appName+packagename
    private var appPositionMap : HashMap<String, Int> = HashMap()
    private var allAppsFragment = AllAppsFragment()
    private var lockedAppsFragment = LockedAppsFragment()
    private var unlockedAppsFragment = UnlockedAppsFragment()

    override fun onListFragmentInteraction(item: AppInfo) {
        val preferences = Util.getSharedPreferences(this)
        if(item.isLocked)
            preferences.edit().putBoolean(item.packageName, item.isLocked).apply()
        else
            preferences.edit().remove(item.packageName).apply()

        val position = appPositionMap[item.packageName]

        appList[position!!].isLocked = item.isLocked
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_allapps -> {

                if(allAppsFragment.list.size == 0) {
                    allAppsFragment.list = appList

                }

                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, allAppsFragment)
                        .commit()


                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_lockedapps -> {

                var filteredAppList = ArrayList<AppInfo>()

                for(app in appList){

                    if(app.isLocked){
                        filteredAppList.add(app)
                    }

                }

                lockedAppsFragment.list = filteredAppList

                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, lockedAppsFragment)
                        .commit()


                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_unlockedapps -> {

                var filteredAppList = ArrayList<AppInfo>()

                for(app in appList){

                    if(!app.isLocked){
                        filteredAppList.add(app)
                    }

                }

                unlockedAppsFragment.list = filteredAppList

                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, unlockedAppsFragment)
                        .commit()

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkSetPin()

        // create the notification
        val m_notificationBuilder = Notification.Builder(this)
                .setContentTitle("App Crypt is active")
                .setContentText("Configure this notification in App Crypt settings.")
                .setSmallIcon(R.drawable.ic_launcher)

        // create the pending intent and add to the notification
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        m_notificationBuilder.setContentIntent(pendingIntent)

        // send the notification
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, m_notificationBuilder.build())

        val startServiceIntent = Intent(this, ApplockService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           // startForegroundService(startServiceIntent)
            startService(startServiceIntent)
        } else {
            startService(startServiceIntent)
        }

        val lockManager = LockManager.getInstance()
        lockManager.enableAppLock(this, CustomPinActivity::class.java)
        lockManager.appLock.timeout = 1000

        toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        Thread( Runnable {

            appList = getAllAppsList()
            allAppsFragment.list = appList

            runOnUiThread(Runnable {
                progressBar.visibility = View.GONE
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, allAppsFragment)
                        .commitAllowingStateLoss()

            })

        }).start()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onResume() {
        super.onResume()

        if (!isAccessGranted()) {
            val builder = AlertDialog.Builder(this);
            builder.setMessage("App needs usage access permission to work.")
            builder.setPositiveButton("OK", DialogInterface.OnClickListener{ dialogInterface, _ ->

                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
                dialogInterface.dismiss()
            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialogInterface, _ ->

                dialogInterface.dismiss()
                finish()

            })
            builder.setCancelable(false)
            usageAccPermDialog = builder.create();
            usageAccPermDialog!!.show()
        }

        if(!Util.getSharedPreferences(this).getBoolean("isAppUnlocked", true)){
            val intent = Intent(this, CustomPinActivity::class.java)
            intent.putExtra("isMainApp", true)
            intent.putExtra("foregroundApp", packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }

    }

    override fun onPause() {
        super.onPause()

        if(usageAccPermDialog != null && usageAccPermDialog!!.isShowing){
            usageAccPermDialog!!.dismiss()
        }

        if(Util.getSharedPreferences(this).getBoolean("isPinSetup", false)) {
            Util.getSharedPreferences(this).edit().putBoolean("isAppUnlocked", false).apply()
        }
    }

    private fun checkSetPin() {

        if(!Util.getSharedPreferences(this).getBoolean("isPinSetup", false)){

            val intent = Intent(this@MainActivity, CustomPinActivity::class.java)
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK)
            intent.putExtra("foregroundApp", packageName)
            startActivityForResult(intent, REQUEST_CODE_ENABLE)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_ENABLE){

            if (data != null) {
                if(data.getBooleanExtra("isPinSetupComplete", false)) {
                    Util.getSharedPreferences(this).edit().putBoolean("isPinSetup", true).apply()
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        // Retrieve the SearchView and plug it into SearchManager
        //val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        //val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return true
    }

    private fun getAllAppsList() : ArrayList<AppInfo> {

        var appList = ArrayList<AppInfo>()
        val packages = packageManager.getInstalledPackages(0) as List<PackageInfo>

        val preferences = Util.getSharedPreferences(this)

        var i = 0
        for (packageInfo in packages){

            val ai = packageManager.getApplicationInfo(packageInfo.packageName, 0) as ApplicationInfo

            if ((ai.flags and (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
                        || packageInfo.packageName.equals(packageName, true)) {
                continue
            }

            var appInfo = AppInfo()
            appInfo.appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
            appInfo.packageName = packageInfo.packageName
            appInfo.icon = packageInfo.applicationInfo.loadIcon(packageManager)
            appInfo.isLocked = preferences.getBoolean(appInfo.packageName, false)

            appPositionMap[appInfo.packageName!!]= i
            appList.add(appInfo)
            i++ // i will be incremented only if the app is not system app.
        }

        return appList
    }

    private fun isAccessGranted(): Boolean {
        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            var mode = 0
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName)
            mode == AppOpsManager.MODE_ALLOWED

        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }

}
