package com.example.myapplication.activities.ui.basket

import android.os.Parcelable



data class CartItem(
    val photoUrl: String,
    val productName: String,
    val productPrice: String,
    val quantity: String,
    val productOldPrice: String,
    val productRating: String,
    val starImg: String,
    val additionToBasket: String,
    val productcolvik: String
)