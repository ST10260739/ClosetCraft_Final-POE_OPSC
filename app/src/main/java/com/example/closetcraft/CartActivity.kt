package com.example.closetcraft

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetcraft.api.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartActivity : AppCompatActivity() {

    private lateinit var adapter: CartAdapter
    private lateinit var firebaseCart: FirebaseCartManager
    private lateinit var rvCartItems: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvDelivery: TextView
    private lateinit var tvDiscount: TextView

    private val deliveryFeeAmount = 50.0
    private val discountAmount = 70.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initialize views
        rvCartItems = findViewById(R.id.rv_cart_items)
        tvSubtotal = findViewById(R.id.tv_subtotal)
        tvTotal = findViewById(R.id.tv_total)
        tvDelivery = findViewById(R.id.tv_delivery_fee)
        tvDiscount = findViewById(R.id.tv_discount)

        firebaseCart = FirebaseCartManager()
        rvCartItems.layoutManager = LinearLayoutManager(this)

        setupNavigation()
        loadCart()
    }

    private fun setupNavigation() {
        // Checkout button
        findViewById<Button>(R.id.btn_checkout).setOnClickListener {
            firebaseCart.getCart { products ->
                val subtotal = products.sumOf { it.price * it.quantity }
                val discount = if (subtotal > 500) discountAmount else 0.0
                val deliveryFee = if (products.isNotEmpty()) if (subtotal > 500) 0.0 else deliveryFeeAmount else 0.0
                val total = subtotal + deliveryFee - discount

                val intent = Intent(this, CheckoutActivity::class.java).apply {
                    putExtra("EXTRA_SUBTOTAL", subtotal)
                    putExtra("EXTRA_DISCOUNT", discount)
                    putExtra("EXTRA_DELIVERY", deliveryFee)
                    putExtra("EXTRA_TOTAL", total)
                }
                startActivity(intent)
            }
        }

        // Bottom navigation
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

    private fun loadCart() {
        firebaseCart.getCart { products ->
            adapter = CartAdapter(
                items = products.toMutableList(),
                onRemove = { product ->
                    firebaseCart.removeFromCart(product.id) {
                        loadCart() // Reload after removal
                    }
                },
                onQuantityChanged = { updatedList ->
                    updateTotals(updatedList)
                    saveUpdatedQuantities(updatedList)
                }
            )
            rvCartItems.adapter = adapter
            updateTotals(products)
        }
    }

    private fun updateTotals(products: List<Product>) {
        val subtotal = products.sumOf { it.price * it.quantity }
        val discount = if (subtotal > 500) discountAmount else 0.0
        val deliveryFee = if (products.isNotEmpty()) if (subtotal > 500) 0.0 else deliveryFeeAmount else 0.0
        val total = subtotal + deliveryFee - discount

        tvSubtotal.text = "R %.2f".format(subtotal)
        tvDelivery.text = when {
            products.isEmpty() -> ""
            deliveryFee == 0.0 -> "FREE"
            else -> "R %.2f".format(deliveryFee)
        }
        tvDiscount.text = if (discount > 0) "-R %.2f".format(discount) else "R 0.00"
        tvTotal.text = "R %.2f".format(total)
    }

    private fun saveUpdatedQuantities(products: List<Product>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        for (product in products) {
            val productRef = db.collection("carts")
                .document(userId)
                .collection("items")
                .document(product.id.toString())

            productRef.update("quantity", product.quantity)
        }
    }
}

