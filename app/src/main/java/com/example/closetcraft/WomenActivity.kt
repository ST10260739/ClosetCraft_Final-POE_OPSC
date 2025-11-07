package com.example.closetcraft

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetcraft.api.Product
import com.example.closetcraft.api.RetrofitClient
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WomenActivity : AppCompatActivity() {

    private lateinit var firebaseCart: FirebaseCartManager
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()
    private val allProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_women)

        firebaseCart = FirebaseCartManager()

        setupRecyclerView()
        setupNavigation()
        setupFilterButton()
        fetchProductsForCategory("women's clothing")
    }

    private fun setupRecyclerView() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(productList) { product ->
            addToCart(product)
        }
        productsRecyclerView.adapter = productAdapter
    }

    private fun fetchProductsForCategory(category: String) {
        RetrofitClient.instance.getProductsByCategory(category)
            .enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    if (response.isSuccessful) {
                        val products = response.body() ?: emptyList()
                        allProducts.clear()
                        allProducts.addAll(products)
                        productList.clear()
                        productList.addAll(products)
                        productAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@WomenActivity, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    Log.e("WomenActivity", "API call failed", t)
                    Toast.makeText(this@WomenActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun addToCart(product: Product) {
        firebaseCart.addToCart(product)
        Toast.makeText(this, "${product.title} added to cart", Toast.LENGTH_SHORT).show()
    }

    private fun setupNavigation() {
        findViewById<LinearLayout>(R.id.nav_cart).setOnClickListener { startActivity(Intent(this, CartActivity::class.java)) }
        findViewById<LinearLayout>(R.id.nav_favourites).setOnClickListener { startActivity(Intent(this, WishlistActivity::class.java)) }
        findViewById<LinearLayout>(R.id.nav_account).setOnClickListener { startActivity(Intent(this, AccountActivity::class.java)) }

        findViewById<MaterialButton>(R.id.womenButton).setOnClickListener {
            startActivity(Intent(this, WomenActivity::class.java))
            finish()
        }

        findViewById<MaterialButton>(R.id.menButton).setOnClickListener {
            startActivity(Intent(this, MenActivity::class.java))
            finish()
        }

    }

    private fun setupFilterButton() {
        findViewById<MaterialButton>(R.id.filterButton).setOnClickListener { showFilterDialog() }
    }

    private fun showFilterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_filter, null)

        val sizeSpinner = dialogView.findViewById<Spinner>(R.id.sizeSpinner)
        val priceSpinner = dialogView.findViewById<Spinner>(R.id.priceSpinner)
        val colorSpinner = dialogView.findViewById<Spinner>(R.id.colorSpinner)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Filter Products")
            .setPositiveButton("Apply") { dialog, _ ->
                val selectedSize = sizeSpinner.selectedItem.toString()
                val selectedPrice = priceSpinner.selectedItem.toString()
                val selectedColor = colorSpinner.selectedItem.toString()
                applyFilters(selectedSize, selectedPrice, selectedColor)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun applyFilters(size: String, price: String, color: String) {
        val filteredList = allProducts.filter { product ->
            var matches = true

            if (price != "All") {
                matches = matches && when (price) {
                    "Under 50" -> product.price < 50
                    "50 - 100" -> product.price in 50.0..100.0
                    "100 - 200" -> product.price in 100.0..200.0
                    "Above 200" -> product.price > 200
                    else -> true
                }
            }

            if (size != "All") {
                matches = matches && product.sizes.any { it.equals(size, ignoreCase = true) }
            }

            if (color != "All") {
                matches = matches && product.colors.any { it.equals(color, ignoreCase = true) }
            }

            matches
        }

        productAdapter.updateData(filteredList)
        Toast.makeText(this, "Filtered ${filteredList.size} items", Toast.LENGTH_SHORT).show()
    }
}
