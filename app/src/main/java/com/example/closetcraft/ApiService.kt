package com.example.closetcraft.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("products")
    fun getLimitedProducts(@Query("limit") count: Int): Call<List<Product>>

    @GET("products/category/{categoryName}")
    fun getProductsByCategory(@Path("categoryName") category: String): Call<List<Product>>
}
