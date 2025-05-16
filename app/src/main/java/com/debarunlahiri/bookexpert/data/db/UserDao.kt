package com.debarunlahiri.bookexpert.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.debarunlahiri.bookexpert.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUser(uid: String): User?
}