package com.debarunlahiri.bookexpert.repository

import com.debarunlahiri.bookexpert.api.ApiService
import com.debarunlahiri.bookexpert.data.db.ItemDao
import com.debarunlahiri.bookexpert.data.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ItemRepository(private val apiService: ApiService, private val itemDao: ItemDao) {
    fun getItems(): Flow<List<Item>> = itemDao.getAllItems()

    suspend fun fetchAndStoreItems() {
        try {
            val items = apiService.getItems()
            withContext(Dispatchers.IO) {
                itemDao.insertItems(items)
            }
        } catch (e: Exception) {
            // Handle network or API errors
        }
    }

    suspend fun updateItem(item: Item) {
        withContext(Dispatchers.IO) {
            itemDao.updateItem(item)
        }
    }

    suspend fun deleteItem(itemId: String) {
        withContext(Dispatchers.IO) {
            itemDao.deleteItem(itemId)
        }
    }
}