package com.kiencute.landmarkremark.preference

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Settings {

    val NIGHT_MODE = intPreferencesKey("night_mode")

    val MODE_NIGHT_DEFAULT =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        } else {
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }

    val USERNAME = stringPreferencesKey("username")
    val USER_ID = stringPreferencesKey("user_id")

}