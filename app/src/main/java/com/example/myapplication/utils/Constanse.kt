package com.example.myapplication.utils

import android.app.Activity
import android.content.ContentResolver.MimeTypeInfo
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Video.Media
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment

object Constanse {
    const val USERS: String = "users"
    const val MYSHOPPAL_PREFERENCE: String = "MyShopPalPref"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MALE: String = "Male"
    const val FEMALE: String = "Female"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"
    const val COMPLETE_PROFILE: String = "profileCompleted"

    fun showImageChooser(fragment: Fragment) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        fragment.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity,uri: Uri?): String? {
        return  MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}