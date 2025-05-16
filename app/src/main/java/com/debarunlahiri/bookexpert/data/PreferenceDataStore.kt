package com.debarunlahiri.bookexpert.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Singleton object for managing DataStore preferences
 */
object PreferenceDataStore {
    // Create a single instance of DataStore
    private val Context.dataStore by preferencesDataStore(name = "settings")
    
    // Keys
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val THEME_MODE = intPreferencesKey("theme_mode")
    
    // Theme mode values
    const val THEME_FOLLOW_SYSTEM = 0
    const val THEME_LIGHT = 1
    const val THEME_DARK = 2
    
    // Get notification preference
    fun getNotificationsEnabled(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }
    }
    
    // Update notification preference
    suspend fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    // Get theme mode preference
    fun getThemeMode(context: Context): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_MODE] ?: THEME_FOLLOW_SYSTEM
        }
    }
    
    // Update theme mode preference
    suspend fun setThemeMode(context: Context, themeMode: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode
        }
    }
} 