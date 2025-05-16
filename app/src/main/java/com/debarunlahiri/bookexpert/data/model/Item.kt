package com.debarunlahiri.bookexpert.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.debarunlahiri.bookexpert.data.db.DataConverter
import com.google.gson.annotations.SerializedName

@Entity(tableName = "items")
@TypeConverters(DataConverter::class)
data class Item(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("data")
    val data: Map<String, Any>? = null
)