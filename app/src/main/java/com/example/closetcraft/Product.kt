package com.example.closetcraft.api

data class Product(
    val id: Int = 0,
    val title: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val image: String = "",
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    var quantity: Int = 1

)
