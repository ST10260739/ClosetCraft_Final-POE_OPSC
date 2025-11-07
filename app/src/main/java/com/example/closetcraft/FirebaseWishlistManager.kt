package com.example.closetcraft

import android.content.Context
import com.example.closetcraft.api.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseWishlistManager {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserWishlistCollection() =
        firestore.collection("wishlists")
            .document(auth.currentUser?.uid ?: "guest")
            .collection("items")

    suspend fun addToWishlist(product: Product) {
        val productMap = hashMapOf(
            "id" to product.id,
            "title" to product.title,
            "price" to product.price,
            "image" to product.image,
            "category" to product.category,
            "description" to product.description
        )
        getUserWishlistCollection().document(product.id.toString()).set(productMap).await()
    }

    suspend fun removeFromWishlist(productId: Int) {
        getUserWishlistCollection().document(productId.toString()).delete().await()
    }

    suspend fun isInWishlist(productId: Int): Boolean {
        val doc = getUserWishlistCollection().document(productId.toString()).get().await()
        return doc.exists()
    }

    suspend fun getAllWishlistItems(): List<Product> {
        val snapshot = getUserWishlistCollection().get().await()
        return snapshot.documents.mapNotNull { doc ->
            val id = (doc.get("id") as? Long)?.toInt() ?: return@mapNotNull null
            val title = doc.getString("title") ?: ""
            val price = (doc.getDouble("price") ?: 0.0)
            val image = doc.getString("image") ?: ""
            val category = doc.getString("category") ?: ""
            val description = doc.getString("description") ?: ""
            Product(id, title, price, description, category, image, emptyList(), emptyList())
        }
    }
}
