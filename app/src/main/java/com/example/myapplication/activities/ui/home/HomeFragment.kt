package com.example.myapplication.activities.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.ui.Product.Product
import com.example.myapplication.activities.ui.Product.ProductAdapter
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var pizzaDatabase: DatabaseReference
    private lateinit var productAdapter: ProductAdapter
    private lateinit var pizzaAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        val recyclerViewPizza: RecyclerView = root.findViewById(R.id.recyclerViewPizza)

        // Настройка RecyclerView для обычных продуктов
        productAdapter = ProductAdapter(emptyList(), isCartFragment = false) // Используйте пустой список для начала
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = productAdapter

        // Настройка RecyclerView для пицц
        pizzaAdapter = ProductAdapter(emptyList(),isCartFragment = false) // Используйте пустой список для начала
        recyclerViewPizza.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewPizza.adapter = pizzaAdapter

        // Подключение к базе данных Firebase для обычных продуктов
        database = FirebaseDatabase.getInstance().reference.child("products")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val photoUrl = productSnapshot.child("photoUrl").getValue(String::class.java) ?: ""
                    val text = productSnapshot.child("text").getValue(String::class.java) ?: ""
                    val starImg = productSnapshot.child("starImg").getValue(String::class.java) ?: ""
                    val productRating = productSnapshot.child("productRating").getValue(String::class.java)?.toFloatOrNull() ?: 0.0f
                    val productPrice = productSnapshot.child("productPrice").getValue(String::class.java) ?: ""
                    val productOldPrice = productSnapshot.child("productOldPrice").getValue(String::class.java)?.toFloatOrNull() ?: 0.0f
                    val additionToBasket = productSnapshot.child("AdditionToBasket").getValue(String::class.java) ?: ""
                    val colvoproduct = productSnapshot.child("productcolvo").getValue(String::class.java) ?: ""

                    val product = Product(photoUrl, text, starImg, productRating, productPrice, productOldPrice, additionToBasket, colvoproduct)

                    productList.add(product)
                }
                productAdapter.submitList(productList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Failed to read database", error.toException())
            }
        })

        // Подключение к базе данных Firebase для пицц
        pizzaDatabase = FirebaseDatabase.getInstance().reference.child("pizzas")
        pizzaDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pizzaList = mutableListOf<Product>()
                for (pizzaSnapshot in snapshot.children) {
                    val photoUrl = pizzaSnapshot.child("photoUrl").getValue(String::class.java) ?: ""
                    val text = pizzaSnapshot.child("text").getValue(String::class.java) ?: ""
                    val starImg = pizzaSnapshot.child("starImg").getValue(String::class.java) ?: ""
                    val productRating = pizzaSnapshot.child("productRating").getValue(String::class.java)?.toFloatOrNull() ?: 0.0f
                    val productPrice = pizzaSnapshot.child("productPrice").getValue(String::class.java) ?: ""
                    val productOldPrice = pizzaSnapshot.child("productOldPrice").getValue(String::class.java)?.toFloatOrNull() ?: 0.0f
                    val additionToBasket = pizzaSnapshot.child("AdditionToBasket").getValue(String::class.java) ?: ""
                    val colvoproduct = pizzaSnapshot.child("productcolvo").getValue(String::class.java) ?: ""

                    val pizza = Product(photoUrl, text, starImg, productRating, productPrice, productOldPrice, additionToBasket,colvoproduct)

                    pizzaList.add(pizza)
                }
                pizzaAdapter.submitList(pizzaList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Failed to read database", error.toException())
            }
        })

        return root
    }
}
