package com.example.myapplication.activities.ui.Product

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.activities.ui.basket.CartItem
import com.example.myapplication.model.User
import com.google.firebase.database.*

class ProductAdapter(
    private var productList: List<Product>,
    private val isCartFragment: Boolean,
    private val isAddToCartEnabled: Boolean = true,
    private val showPlusButton: Boolean = true
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productPhoto: ImageView = view.findViewById(R.id.product_photo)
        val productName: TextView = view.findViewById(R.id.product_name)
        val priceProduct: TextView = view.findViewById(R.id.price_textView)
        val oldPriceProduct: TextView = view.findViewById(R.id.old_price_text)
        val starProduct: ImageView = view.findViewById(R.id.Star)
        val additionToCartButton: ImageView = view.findViewById(R.id.Addition)
        val removeButton: ImageView = view.findViewById(R.id.Delete)
        val subtractButton: ImageView = view.findViewById(R.id.Substraction)
        val addButton: ImageView = view.findViewById(R.id.Additioncolvoproduct)
        val productRating: TextView = view.findViewById(R.id.product_rating)
        val productQuantity: TextView = view.findViewById(R.id.product_colvo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        Glide.with(holder.itemView.context)
            .load(product.photoUrl)
            .into(holder.productPhoto)
        holder.productName.text = product.text
        holder.priceProduct.text = product.productPrice
        holder.oldPriceProduct.text = product.productOldPrice.toString()
        holder.productRating.text = product.productRating.toString()
        holder.productQuantity.text = product.productcolvo.toString()
        Glide.with(holder.itemView.context)
            .load(product.starImg)
            .into(holder.starProduct)

        if (isCartFragment) {
            holder.additionToCartButton.visibility = View.GONE
            holder.removeButton.visibility = View.VISIBLE
        } else {
            if (isAddToCartEnabled && showPlusButton) {
                Glide.with(holder.itemView.context)
                    .load(product.additionToBasket)
                    .into(holder.additionToCartButton)
                holder.additionToCartButton.setOnClickListener {
                    val cartItem = CartItem(
                        product.photoUrl,
                        product.text,
                        product.productPrice,
                        "1",
                        product.productOldPrice.toString(),
                        product.productRating.toString(),
                        product.starImg,
                        product.additionToBasket,
                        product.productcolvo
                    )
                    val basketReference = FirebaseDatabase.getInstance().reference.child("basket")
                    basketReference.push().setValue(cartItem)
                    Toast.makeText(
                        holder.itemView.context,
                        "Товар добавлен в корзину",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                holder.additionToCartButton.visibility = View.GONE
            }
            holder.removeButton.visibility = View.GONE
        }

        holder.removeButton.setOnClickListener {
            val productName = product.text.toString().trim() // Убираем лишние пробелы
            val productReference =
                FirebaseDatabase.getInstance().reference.child("basket")
            productReference.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Товар удален из корзины",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Удаление товара из списка и обновление адаптера
                    productList =
                        productList.filterIndexed { index, _ -> index != holder.adapterPosition }
                    notifyItemRemoved(holder.adapterPosition)
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Ошибка при удалении товара",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        holder.addButton.setOnClickListener {
            val currentQuantityStr = holder.productQuantity.text.toString()
            if (currentQuantityStr.isNotEmpty()) {
                val currentQuantity = currentQuantityStr.toInt()
                val newQuantity = currentQuantity + 1
                holder.productQuantity.text = newQuantity.toString()
                updateCartItemQuantity(product.text, newQuantity)
                supdateCartItemQuantity(product.text, newQuantity)
            }
        }

        holder.subtractButton.setOnClickListener {
            val currentQuantityStr = holder.productQuantity.text.toString()
            if (currentQuantityStr.isNotEmpty()) {
                val currentQuantity = currentQuantityStr.toInt()
                if (currentQuantity > 0) {
                    val newQuantity = currentQuantity - 1
                    holder.productQuantity.text = newQuantity.toString()
                    updateCartItemQuantity(product.text, newQuantity)
                    supdateCartItemQuantity(product.text, newQuantity)
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Добавьте товар",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun submitList(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }

    private fun updateCartItemQuantity(productName: String, newQuantity: Int) {
        val cartItemReference = FirebaseDatabase.getInstance().reference.child("products")
        val query: Query = cartItemReference.orderByChild("productName").equalTo(productName)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (productSnapshot in snapshot.children) {
                    productSnapshot.ref.child("productcolvo").setValue(newQuantity.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProductAdapter", "Failed to update quantity in database", error.toException())
            }
        })
    }
    private fun supdateCartItemQuantity(productName: String, newQuantity: Int) {
        val cartItemReference = FirebaseDatabase.getInstance().reference.child("basket")
        val query: Query = cartItemReference.orderByChild("productName").equalTo(productName)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (productSnapshot in snapshot.children) {
                    productSnapshot.ref.child("productcolvik").setValue(newQuantity.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProductAdapter", "Failed to update quantity in database", error.toException())
            }
        })
    }
}