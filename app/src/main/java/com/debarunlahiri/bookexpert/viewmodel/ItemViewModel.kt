package com.debarunlahiri.bookexpert.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.debarunlahiri.bookexpert.api.ApiService
import com.debarunlahiri.bookexpert.data.PreferenceDataStore
import com.debarunlahiri.bookexpert.data.db.AppDatabase
import com.debarunlahiri.bookexpert.data.model.Item
import com.debarunlahiri.bookexpert.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItemRepository
    val items: Flow<List<Item>>

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.restful-api.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)
        val itemDao = AppDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(apiService, itemDao)
        items = repository.getItems()
        viewModelScope.launch {
            repository.fetchAndStoreItems()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "item_channel"
            val channelName = "Item Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Notifications for item actions"
            }
            val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateItem(item: Item) = viewModelScope.launch {
        repository.updateItem(item)
    }

    fun deleteItem(item: Item) = viewModelScope.launch {
        repository.deleteItem(item.id)
        // Check if notifications are enabled in preferences
        val notificationsEnabled = PreferenceDataStore
            .getNotificationsEnabled(getApplication())
            .first()
        
        if (notificationsEnabled) {
            sendItemDeletedNotification(item)
        }
    }

    private fun sendItemDeletedNotification(item: Item) {
        val title = "Item Deleted"
        val contentText = "Item ${item.name} has been deleted"
        
        // Build detailed notification content
        val stringBuilder = StringBuilder()
        stringBuilder.append("ID: ${item.id}\n")
        stringBuilder.append("Name: ${item.name}\n")
        
        // Add any additional data if available
        item.data?.forEach { (key, value) ->
            stringBuilder.append("$key: $value\n")
        }
        
        val bigText = stringBuilder.toString()
        
        sendNotification(
            getApplication(),
            title,
            contentText,
            bigText
        )
    }

    private fun sendNotification(
        context: Context, 
        title: String, 
        message: String,
        bigText: String? = null
    ) {
        val channelId = "item_channel"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        
        // Use big text style for detailed item information
        if (bigText != null) {
            val bigTextStyle = NotificationCompat.BigTextStyle()
                .bigText(bigText)
            notificationBuilder.setStyle(bigTextStyle)
        }
        
        val notification = notificationBuilder.build()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    ?: throw IllegalArgumentException("Application cannot be null")
                if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
                    return ItemViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}