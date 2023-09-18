package com.example.myapplication.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import com.example.myapplication.R
import java.io.IOException

class GlideLoader(val context : Context) {

    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_account_box_24)
                .into(imageView)
        }catch (e: IOException) {
            e.printStackTrace()
        }
    }

}