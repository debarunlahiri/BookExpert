package com.debarunlahiri.bookexpert.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.debarunlahiri.bookexpert.data.PreferenceDataStore

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun AppTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemInDarkTheme = isSystemInDarkTheme()
    
    // Get theme mode from preferences
    val themeModeFlow = remember { PreferenceDataStore.getThemeMode(context) }
    val themeMode by themeModeFlow.collectAsState(initial = PreferenceDataStore.THEME_FOLLOW_SYSTEM)
    
    // Determine if dark theme should be used based on preferences
    val darkTheme = when (themeMode) {
        PreferenceDataStore.THEME_LIGHT -> false
        PreferenceDataStore.THEME_DARK -> true
        else -> systemInDarkTheme // THEME_FOLLOW_SYSTEM
    }
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}