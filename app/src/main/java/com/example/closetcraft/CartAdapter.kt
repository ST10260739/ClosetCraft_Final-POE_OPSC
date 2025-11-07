package com.example.closetcraft

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.closetcraft.api.Product

class CartAdapter(
    private val items: MutableList<Product>,
    private val onRemove: (Product) -> Unit,
    private val onQuantityChanged: (List<Product>) -> Unit // 👈 notify CartActivity to recalc totals
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProductImage: ImageView = view.findViewById(R.id.ivProductImage)
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val ivRemoveItem: ImageView = view.findViewById(R.id.ivRemoveItem)
        val ivIncreaseQuantity: ImageView = view.findViewById(R.id.ivIncreaseQuantity)
        val ivDecreaseQuantity: ImageView = view.findViewById(R.id.ivDecreaseQuantity)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = items[position]

        holder.tvProductName.text = product.title
        holder.tvProductPrice.text = "R ${String.format("%.2f", product.price * product.quantity)}"
        holder.tvQuantity.text = product.quantity.toString()

        holder.ivProductImage.load(product.image) {
            crossfade(true)
        }

        holder.ivIncreaseQuantity.setOnClickListener {
            product.quantity++
            notifyItemChanged(position)
            onQuantityChanged(items)
        }

        holder.ivDecreaseQuantity.setOnClickListener {
            if (product.quantity > 1) {
                product.quantity--
                notifyItemChanged(position)
                onQuantityChanged(items)
            }
        }

        holder.ivRemoveItem.setOnClickListener {
            onRemove(product)
        }
    }

    override fun getItemCount(): Int = items.size
}
