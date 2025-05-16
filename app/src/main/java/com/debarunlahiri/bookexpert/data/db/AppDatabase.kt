package com.debarunlahiri.bookexpert.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.debarunlahiri.bookexpert.data.model.Item
import com.debarunlahiri.bookexpert.data.model.User

@Database(entities = [User::class, Item::class], version = 2, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // For development only
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}