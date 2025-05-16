package com.debarunlahiri.bookexpert

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debarunlahiri.bookexpert.data.PreferenceDataStore
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val notificationsEnabledFlow = remember {
        PreferenceDataStore.getNotificationsEnabled(context)
    }
    val notificationsEnabled by notificationsEnabledFlow.collectAsState(initial = true)
    
    // Get theme mode from preferences
    val themeModeFlow = remember {
        PreferenceDataStore.getThemeMode(context)
    }
    val themeMode by themeModeFlow.collectAsState(initial = PreferenceDataStore.THEME_FOLLOW_SYSTEM)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )
        
        // Theme Settings Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // System Default Theme Option
                ThemeOption(
                    title = "System Default",
                    subtitle = "Follow system theme settings",
                    isSelected = themeMode == PreferenceDataStore.THEME_FOLLOW_SYSTEM,
                    onClick = {
                        scope.launch {
                            PreferenceDataStore.setThemeMode(context, PreferenceDataStore.THEME_FOLLOW_SYSTEM)
                        }
                    }
                )
                
                // Light Theme Option
                ThemeOption(
                    title = "Light",
                    subtitle = "Always use light theme",
                    isSelected = themeMode == PreferenceDataStore.THEME_LIGHT,
                    onClick = {
                        scope.launch {
                            PreferenceDataStore.setThemeMode(context, PreferenceDataStore.THEME_LIGHT)
                        }
                    }
                )
                
                // Dark Theme Option
                ThemeOption(
                    title = "Dark",
                    subtitle = "Always use dark theme",
                    isSelected = themeMode == PreferenceDataStore.THEME_DARK,
                    onClick = {
                        scope.launch {
                            PreferenceDataStore.setThemeMode(context, PreferenceDataStore.THEME_DARK)
                        }
                    }
                )
            }
        }
        
        // Notifications Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Item Deletion Notifications",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Receive notifications when items are deleted",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { isChecked ->
                            scope.launch {
                                PreferenceDataStore.setNotificationsEnabled(context, isChecked)
                            }
                        }
                    )
                }
            }
        }
        
        // Add information about notification content
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "About Item Deletion Notifications",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "When enabled, you'll receive detailed notifications whenever an item is deleted. The notification will include the item's name, ID, and any additional data associated with it.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
    }
}