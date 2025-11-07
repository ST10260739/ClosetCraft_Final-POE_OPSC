package com.example.closetcraft

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.closetcraft.api.Product
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.button.MaterialButton

class ProductAdapter(
    private val products: MutableList<Product>,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productSizesFlex: FlexboxLayout = itemView.findViewById(R.id.productSizesFlex)
        val productColorsFlex: FlexboxLayout = itemView.findViewById(R.id.productColorsFlex)
        val btnAddToCart: MaterialButton = itemView.findViewById(R.id.btnAddToCart)
        val heartIcon: ImageView = itemView.findViewById(R.id.heartIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val context = holder.itemView.context
        val wishlistManager = WishlistManager(context)

        // Title & price
        holder.productTitle.text = product.title
        holder.productPrice.text = "R ${String.format("%.2f", product.price)}"

        // Load image
        holder.productImage.load(product.image) {
            placeholder(R.drawable.image_placeholder)
            crossfade(true)
        }

        // Clear previous chips
        holder.productSizesFlex.removeAllViews()
        holder.productColorsFlex.removeAllViews()

        // Sizes
        product.sizes.forEach { size ->
            val chip = createTextChip(context, size)
            holder.productSizesFlex.addView(chip)
        }

        // Colors
        product.colors.forEach { color ->
            val swatch = createColorSwatch(context, color)
            holder.productColorsFlex.addView(swatch)
        }

        // Check if product is already in wishlist
        wishlistManager.checkIfInWishlist(product) { isWishlisted ->
            holder.heartIcon.setImageResource(
                if (isWishlisted) R.drawable.favorites else R.drawable.favorites
            )
        }

        // Handle heart icon clicks
        holder.heartIcon.setOnClickListener {
            wishlistManager.checkIfInWishlist(product) { isWishlisted ->
                if (isWishlisted) {
                    wishlistManager.removeFromWishlist(product)
                    holder.heartIcon.setImageResource(R.drawable.favorites)
                } else {
                    wishlistManager.addToWishlist(product)
                    holder.heartIcon.setImageResource(R.drawable.favorites)
                }
            }
        }

        // Add to cart
        holder.btnAddToCart.setOnClickListener { onAddToCart(product) }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newList: List<Product>) {
        products.clear()
        products.addAll(newList)
        notifyDataSetChanged()
    }

    private fun createTextChip(context: Context, text: String): TextView {
        val chip = TextView(context)
        chip.text = text
        chip.textSize = 12f
        chip.setTextColor(Color.WHITE)
        chip.setBackgroundResource(R.drawable.rounded_chip_bg)
        chip.gravity = Gravity.CENTER
        chip.setPadding(24, 8, 24, 8)
        val params = FlexboxLayout.LayoutParams(
            FlexboxLayout.LayoutParams.WRAP_CONTENT,
            FlexboxLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(8, 8, 8, 8)
        chip.layoutParams = params
        return chip
    }

    private fun createColorSwatch(context: Context, colorName: String): View {
        val view = View(context)
        val size = context.resources.getDimensionPixelSize(R.dimen.chip_size)
        val params = FlexboxLayout.LayoutParams(size, size)
        params.setMargins(8, 8, 8, 8)
        view.layoutParams = params

        val parsedColor = try {
            Color.parseColor(colorName.lowercase())
        } catch (e: IllegalArgumentException) {
            Color.LTGRAY
        }
        view.setBackgroundColor(parsedColor)

        view.setOnClickListener {
            Toast.makeText(context, colorName, Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
