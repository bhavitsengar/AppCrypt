package ml.bhavitsengar.appcrypt

import android.content.Context
import android.content.SharedPreferences

class Util {

    companion object {

        fun getSharedPreferences(context: Context) : SharedPreferences{

            return context.getSharedPreferences(context.getString(R.string.shared_preference_key),Context.MODE_PRIVATE)

        }

    }

}