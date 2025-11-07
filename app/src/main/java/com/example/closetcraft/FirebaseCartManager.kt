package com.example.closetcraft

import android.util.Log
import com.example.closetcraft.api.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCartManager {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Helper function for user's cart collection
    private fun userCartCollection() =
        db.collection("carts")
            .document(auth.currentUser?.uid ?: "guest_user")
            .collection("items")

    // --- Add product to Firestore cart ---
    fun addToCart(product: Product) {
        val docRef = userCartCollection().document(product.id.toString()) // convert Int → String
        docRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // If product exists → increase quantity
                    val existingQty = snapshot.getLong("quantity")?.toInt() ?: 1
                    docRef.update("quantity", existingQty + 1)
                        .addOnSuccessListener {
                            Log.d("FirebaseCartManager", "Increased quantity for ${product.title}")
                        }
                } else {
                    // Add new product with quantity = 1
                    val productData = product.copy(quantity = 1)
                    docRef.set(productData)
                        .addOnSuccessListener {
                            Log.d("FirebaseCartManager", "Added to cart: ${product.title}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseCartManager", "Failed to add to cart", e)
            }
    }

    // --- Remove product from Firestore cart ---
    fun removeFromCart(productId: Int, callback: (() -> Unit)? = null) {
        userCartCollection().document(productId.toString()).delete()
            .addOnSuccessListener {
                Log.d("FirebaseCartManager", "Removed product: $productId")
                callback?.invoke()
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseCartManager", "Failed to remove product", e)
            }
    }

    // --- Fetch all products from Firestore cart ---
    fun getCart(callback: (List<Product>) -> Unit) {
        userCartCollection().get()
            .addOnSuccessListener { querySnapshot ->
                val products = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)
                }
                callback(products)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseCartManager", "Failed to fetch cart", e)
                callback(emptyList())
            }
    }

    // --- Update quantity for a specific product ---
    fun updateQuantity(productId: Int, newQuantity: Int, onComplete: (() -> Unit)? = null) {
        val docRef = userCartCollection().document(productId.toString()) // convert Int → String
        if (newQuantity <= 0) {
            removeFromCart(productId, onComplete)
            return
        }

        docRef.update("quantity", newQuantity)
            .addOnSuccessListener {
                Log.d("FirebaseCartManager", "Updated quantity for $productId: $newQuantity")
                onComplete?.invoke()
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseCartManager", "Failed to update quantity", e)
            }
    }
}
