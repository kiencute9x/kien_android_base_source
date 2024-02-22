package com.kiencute.landmarkremark.datastore

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.kiencute.landmarkremark.preference.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {

    private val settingsDataStore = appContext.dataStore

    // DarkMode

    suspend fun setThemeMode(mode: Int) {
        settingsDataStore.edit { settings ->
            settings[Settings.NIGHT_MODE] = mode
        }
    }

    val themeMode: Flow<Int> = settingsDataStore.data.map { preferences ->
        preferences[Settings.NIGHT_MODE] ?: AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
    }

    // User name

    suspend fun setUserName(username: String) {
        settingsDataStore.edit { settings ->
            settings[Settings.USERNAME] = username
        }
    }

    val username: Flow<String?> = settingsDataStore.data.map { preferences ->
        preferences[Settings.USERNAME] ?: ""
    }

    // User id
    suspend fun setUserId(userId: String) {
        settingsDataStore.edit { settings ->
            settings[Settings.USER_ID] = userId
        }
    }

    val userId: Flow<String?> = settingsDataStore.data.map { preferences ->
        preferences[Settings.USER_ID] ?: ""
    }


}