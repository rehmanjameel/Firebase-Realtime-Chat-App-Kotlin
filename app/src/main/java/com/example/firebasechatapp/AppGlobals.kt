package com.example.firebasechatapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

@SuppressLint("CommitPrefEdits", "StaticFieldLeak")
class AppGlobals : Application() {

    init {
        context = this
    }

    companion object {
        private var context: Context? = null

        private lateinit var sharedPref: SharedPreferences

        fun applicationContext() : Context {
            return context!!.applicationContext
        }

        val PREFS_NAME = "sharedPrefs"
        val KEY_LOGGED_IN = "login_key"
//        const val SERVER = "http://192.168.100.150:8000"
//        const val SERVER = "https://yad-dahani.herokuapp.com"
//        private val API = String.format("%s/api/", SERVER)
//        val REGISTER_API = String.format("%sregister/", API)
//        val REQUEST_OTP_API = String.format("%srequest_otp/", API)
//        val ACTIVATE_ACCOUNT_API = String.format("%sactivate_account/", API)
//        val LOGIN_API = String.format("%slogin/", API)
//        val SEARCH_USER_API = String.format("%ssearch_user/", API)
//        val SEND_FRIEND_REQUEST_API = String.format("%spending_requests/", API)
//        val GET_FRIEND_REQUEST_API = String.format("%spending_requests/", API)
//        val DELETE_FRIEND_REQUEST_API = String.format("%spending_requests/", API)
//        val ACCEPT_FRIEND_REQUEST_API = String.format("%sfriends/", API)
//        val FRIENDS_LIST_API = String.format("%sfriends/", API)
//        val DELETE_FRIEND_API = String.format("%sfriends/", API)
//        val PROFILE_GET_API = String.format("%sprofile/", API)
//        val UPDATE_PROFILE_API = String.format("%sprofile/", API)
//        val DELETE_PROFILE_API = String.format("%sprofile/", API)
//        val FCM_TOKEN_UPDATE_API = String.format("%sprofile/", API)
//        val CREATE_REMINDER_API = String.format("%sreminder/", API)
//        val GET_REMINDERS_LIST_API = String.format("%sreminder/", API)
//        val UPDATE_REMINDERS_API = String.format("%sreminder/", API)
//        val DELETE_REMINDERS_API = String.format("%sreminder/", API)
    }

    override fun onCreate() {
        super.onCreate()
        val myContext: Context = applicationContext()
        Log.e("Check ", "yes")

        sharedPref = myContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    }


    fun saveString(KEY_NAME: String, text: String) {

        Log.e("Check ", "Here")

        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putString(KEY_NAME, text)

        editor.apply()
    }

    fun saveInt(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putInt(KEY_NAME, value)

        editor.apply()
    }

    fun saveLoginOrBoolean(status: Boolean) {

        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putBoolean(KEY_LOGGED_IN, status)

        editor.apply()
    }

    fun getValueString(KEY_NAME: String): String? {

        return sharedPref.getString(KEY_NAME, null)

    }

    fun getValueInt(KEY_NAME: String): Int {

        return sharedPref.getInt(KEY_NAME, 0)
    }


    fun isLoggedInOrGetValueBoolean(): Boolean {

        return sharedPref.getBoolean(KEY_LOGGED_IN, false)

    }

    fun logoutOrClearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.clear()
        editor.apply()
    }

    fun removeValue(KEY_NAME: String) {

        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.remove(KEY_NAME)
        editor.apply()
    }


}