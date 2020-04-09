package com.toandv.networkindicator.data

import android.content.Context
import androidx.preference.PreferenceManager
import com.toandv.networkindicator.R
import com.toandv.networkindicator.utils.Constants.KEY_ON_OFF

object PreferenceHelper {

    @JvmStatic
    fun running(context: Context?, b: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_ON_OFF, b)
                .apply()
    }

    @JvmStatic
    fun isRunning(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_ON_OFF, false)
    }

    @JvmStatic
    fun isHideOnDisconnected(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_hide), true)
    }

    @JvmStatic
    fun isStartOnBoot(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_boot), true)
    }

    @JvmStatic
    fun isHideFromLockScreen(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_lock_screen), true)
    }
}