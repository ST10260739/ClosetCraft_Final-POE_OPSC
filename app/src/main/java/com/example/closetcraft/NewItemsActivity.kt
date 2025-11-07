package com.example.closetcraft

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetcraft.api.Product
import com.example.closetcraft.api.RetrofitClient
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewItemsActivity : AppCompatActivity() {

    private lateinit var firebaseCart: FirebaseCartManager
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_items)



        firebaseCart = FirebaseCartManager()

        setupRecyclerView()
        setupNavigation()
        fetchProductsForCategory("electronics")
    }

    // --- Setup RecyclerView ---
    private fun setupRecyclerView() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView)

        // Use GridLayoutManager for 3 columns
        val layoutManager = GridLayoutManager(this, 3)
        productsRecyclerView.layoutManager = layoutManager

        productAdapter = ProductAdapter(productList) { product ->
            // Handle add to cart
            addToCart(product)
        }
        productsRecyclerView.adapter = productAdapter
    }

    // --- Fetch products using Retrofit ---
    private fun fetchProductsForCategory(category: String) {
        RetrofitClient.instance.getProductsByCategory(category)
            .enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    if (response.isSuccessful) {
                        val products = response.body()
                        if (products != null && products.isNotEmpty()) {
                            productList.clear()
                            productList.addAll(products)
                            productAdapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(this@NewItemsActivity, "No products found.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            this@NewItemsActivity,
                            "Failed to load products. Code: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    Log.e("WomenActivity", "API call failed", t)
                    Toast.makeText(this@NewItemsActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // --- Add to cart functionality ---
    private fun addToCart(product: Product) {
        firebaseCart.addToCart(product)
        Toast.makeText(this, "${product.title} added to cart", Toast.LENGTH_SHORT).show()
    }

    // --- Bottom navigation setup ---
    private fun setupNavigation() {
        findViewById<LinearLayout>(R.id.nav_new).setOnClickListener {
            startActivity(Intent(this, NewItemsActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_cart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_favourites).setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_account).setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }

        // Tab buttons
        findViewById<MaterialButton>(R.id.womenButton).setOnClickListener {
            startActivity(Intent(this, WomenActivity::class.java))
            finish()
        }
        findViewById<MaterialButton>(R.id.menButton).setOnClickListener {
            startActivity(Intent(this, MenActivity::class.java))
            finish()
        }
        findViewById<MaterialButton>(R.id.kidsButton).setOnClickListener {
            startActivity(Intent(this, KidsActivity::class.java))
            finish()
        }
    }
}