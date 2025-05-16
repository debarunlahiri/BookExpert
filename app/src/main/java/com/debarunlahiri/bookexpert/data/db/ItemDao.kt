package com.debarunlahiri.bookexpert.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.debarunlahiri.bookexpert.data.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItems(items: List<Item>): List<Long> // Return List<Long> for inserted row IDs

    @Update
    fun updateItem(item: Item): Int // Return Int for number of updated rows

    @Query("DELETE FROM items WHERE id = :itemId")
    fun deleteItem(itemId: String): Int // Return Int for number of deleted rows
}