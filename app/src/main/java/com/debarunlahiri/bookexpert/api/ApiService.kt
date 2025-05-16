package com.debarunlahiri.bookexpert.api

import com.debarunlahiri.bookexpert.data.model.Item
import retrofit2.http.GET

interface ApiService {
    @GET("objects")
    suspend fun getItems(): List<Item>
}