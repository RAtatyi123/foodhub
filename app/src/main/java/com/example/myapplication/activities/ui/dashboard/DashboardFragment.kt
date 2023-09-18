package com.example.myapplication.activities.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.ui.Product.Product
import com.example.myapplication.activities.ui.Product.ProductAdapter
import com.example.myapplication.activities.ui.basket.CartItem
import com.example.myapplication.databinding.FragmentDashboardBinding
import com.example.myapplication.utils.MSPButton
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class DashboardFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)

        // Настройка RecyclerView
        productAdapter = ProductAdapter(emptyList(), isCartFragment = true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = productAdapter

        // Подключение к базе данных Firebase
        database = FirebaseDatabase.getInstance().reference.child("basket")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val productId = productSnapshot.child("photoUrl").getValue(String::class.java) ?: ""
                    val productName = productSnapshot.child("productName").getValue(String::class.java) ?: ""
                    val productPrice = productSnapshot.child("productPrice").getValue(String::class.java) ?: ""
                    val productOldPrice = productSnapshot.child("productOldPrice").getValue()?.toString()?.toFloatOrNull() ?: 0.0f
                    val productRating = productSnapshot.child("productRating").getValue()?.toString()?.toFloatOrNull() ?: 0.0f
                    val starImg = productSnapshot.child("starImg").getValue(String::class.java) ?: ""
                    val additionToBasket = productSnapshot.child("additionToBasket").getValue(String::class.java) ?: ""
                    val colvoproduct = productSnapshot.child("productcolvik").getValue(String::class.java) ?: ""


                    val product = Product(productId, productName, starImg, productRating, productPrice, productOldPrice, additionToBasket, colvoproduct)
                    productList.add(product)
                }
                productAdapter.submitList(productList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DashboardFragment", "Failed to read database", error.toException())
            }
        })

        val btnAddOrder: MSPButton = root.findViewById(R.id.btn_add_order)
        btnAddOrder.setOnClickListener {
            moveDataFromBasketToMyOrder()
        }

        return root
    }

    private fun moveDataFromBasketToMyOrder() {
        val basketReference = FirebaseDatabase.getInstance().reference.child("basket")
        val myOrderReference = FirebaseDatabase.getInstance().reference.child("myorder")

        basketReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (productSnapshot in snapshot.children) {
                    // Получение данных о товаре из коллекции "basket"
                    val productId = productSnapshot.child("photoUrl").getValue(String::class.java) ?: ""
                    val productName = productSnapshot.child("productName").getValue(String::class.java) ?: ""
                    val productPrice = productSnapshot.child("productPrice").getValue(String::class.java) ?: ""
                    val productOldPrice = productSnapshot.child("productOldPrice").getValue()?.toString()?.toFloatOrNull() ?: 0.0f
                    val productRating = productSnapshot.child("productRating").getValue()?.toString()?.toFloatOrNull() ?: 0.0f
                    val starImg = productSnapshot.child("starImg").getValue(String::class.java) ?: ""
                    val additionToBasket = productSnapshot.child("additionToBasket").getValue(String::class.java) ?: ""
                    val colvoproduct = productSnapshot.child("productcolvo").getValue(String::class.java) ?: ""


                    // Создание нового узла в коллекции "myorder" и добавление данных о товаре
                    val newProduct = myOrderReference.push()
                    newProduct.child("photoUrl").setValue(productId)
                    newProduct.child("productName").setValue(productName)
                    newProduct.child("productPrice").setValue(productPrice)
                    newProduct.child("productOldPrice").setValue(productOldPrice)
                    newProduct.child("productRating").setValue(productRating)
                    newProduct.child("starImg").setValue(starImg)
                    newProduct.child("additionToBasket").setValue(additionToBasket)
                }

                // Очистка коллекции "basket"
                basketReference.removeValue()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DashboardFragment", "Failed to read database", error.toException())
            }

        })
    }

    private fun updateCartItemQuantity(productName: String, quantity: Int) {
        val cartItemReference = FirebaseDatabase.getInstance().reference.child("basket").child(productName)
        cartItemReference.child("productcolvo").setValue(quantity.toString())
    }
}
