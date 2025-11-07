package com.example.closetcraft

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetcraft.api.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WishlistActivity : AppCompatActivity() {

    private lateinit var wishlistRecyclerView: RecyclerView
    private lateinit var wishlistAdapter: ProductAdapter
    private val wishlistItems = mutableListOf<Product>()
    private lateinit var firebaseCart: FirebaseCartManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        firebaseCart = FirebaseCartManager()

        // Initialize RecyclerView
        wishlistRecyclerView = findViewById(R.id.wishlistRecyclerView)
        wishlistRecyclerView.layoutManager = LinearLayoutManager(this)
        wishlistAdapter = ProductAdapter(wishlistItems) { product ->
            addToCart(product)
        }
        wishlistRecyclerView.adapter = wishlistAdapter

        // Load wishlist from Firestore
        loadWishlist()

        // Setup bottom navigation
        setupNavigation()
    }

    private fun loadWishlist() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Please log in to view your wishlist", Toast.LENGTH_SHORT).show()
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("wishlists")
            .document(userId)
            .collection("items")
            .get()
            .addOnSuccessListener { documents ->
                wishlistItems.clear()
                wishlistItems.addAll(documents.mapNotNull { it.toObject(Product::class.java) })
                wishlistAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load wishlist", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addToCart(product: Product) {
        firebaseCart.addToCart(product)
        Toast.makeText(this, "${product.title} added to cart", Toast.LENGTH_SHORT).show()
    }

    private fun setupNavigation() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, WomenActivity::class.java))
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
    }
}
