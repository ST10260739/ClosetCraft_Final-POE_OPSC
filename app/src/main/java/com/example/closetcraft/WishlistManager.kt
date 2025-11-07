package com.example.closetcraft

import android.content.Context
import android.widget.Toast
import com.example.closetcraft.api.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WishlistManager(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun addToWishlist(product: Product) {
        if (userId == null) {
            Toast.makeText(context, "Please log in first", Toast.LENGTH_SHORT).show()
            return
        }

        val wishlistRef = firestore.collection("wishlists").document(userId)
            .collection("items").document(product.id.toString())

        wishlistRef.set(product)
            .addOnSuccessListener {
                Toast.makeText(context, "${product.title} added to wishlist", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
    }

    fun removeFromWishlist(product: Product) {
        if (userId == null) return

        val wishlistRef = firestore.collection("wishlists").document(userId)
            .collection("items").document(product.id.toString())

        wishlistRef.delete()
            .addOnSuccessListener {
                Toast.makeText(context, "${product.title} removed from wishlist", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show()
            }
    }

    fun checkIfInWishlist(product: Product, callback: (Boolean) -> Unit) {
        if (userId == null) {
            callback(false)
            return
        }

        firestore.collection("wishlists").document(userId)
            .collection("items").document(product.id.toString())
            .get()
            .addOnSuccessListener { document ->
                callback(document.exists())
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}
