package com.nks.interactive.multimediapanel.localStorage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

class AppDataStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("data",MODE_PRIVATE)

    var ipAddress: String
        get() = prefs.getString("ipAddress","") ?: ""
        set(value) = prefs.edit { putString("ipAddress", value) }

    var port: String
        get() = prefs.getString("port","") ?: ""
        set(value) = prefs.edit { putString("port", value) }
}