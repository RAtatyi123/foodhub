package com.example.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.ui.Product.Product
import com.example.myapplication.activities.ui.Product.ProductAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyOrderActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order)

        recyclerView = findViewById(R.id.recyclerViewMyOrder)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        productAdapter = ProductAdapter(emptyList(), isCartFragment = false, isAddToCartEnabled = false,showPlusButton = false)
        recyclerView.adapter = productAdapter

        val databaseReference = FirebaseDatabase.getInstance().reference.child("myorder")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val productId = productSnapshot.child("photoUrl").getValue(String::class.java) ?: ""
                    val productName = productSnapshot.child("productName").getValue(String::class.java) ?: ""
                    val productPrice = productSnapshot.child("productPrice").getValue(String::class.java) ?: ""
                    val productOldPrice =
                        productSnapshot.child("productOldPrice").getValue()?.toString()?.toFloatOrNull() ?: 0.0f
                    val productRating =
                        productSnapshot.child("productRating").getValue()?.toString()?.toFloatOrNull() ?: 0.0f
                    val starImg = productSnapshot.child("starImg").getValue(String::class.java) ?: ""
                    val additionToBasket = productSnapshot.child("additionToBasket").getValue(String::class.java)
                        ?: ""

                    val product = Product(
                        productId,
                        productName,
                        starImg,
                        productRating,
                        productPrice,
                        productOldPrice,
                        additionToBasket
                    )
                    productList.add(product)
                }
                productAdapter.submitList(productList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MyOrderActivity", "Failed to read database", error.toException())
            }
        })
    }
}