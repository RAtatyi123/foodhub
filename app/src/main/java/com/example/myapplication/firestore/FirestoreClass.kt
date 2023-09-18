package com.example.myapplication.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.myapplication.activities.*
import com.example.myapplication.activities.ui.notifications.NotificationsFragment
import com.example.myapplication.fragments.LoginFragment
import com.example.myapplication.fragments.RegisterFragment
import com.example.myapplication.fragments.UserProfileFragment
import com.example.myapplication.model.User
import com.example.myapplication.utils.Constanse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FirestoreClass(private val context: Context) {

    private val mFirestore = FirebaseFirestore.getInstance()


    fun registerUser(fragment: RegisterFragment, user: User) {
        mFirestore.collection(Constanse.USERS)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                fragment.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error writing document", e)
            }
    }

    fun getCurrentUserID(): String {
        val currrentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currrentUser != null) {
            currentUserID = currrentUser.uid
        }
        return currentUserID

    }

    fun getUserDetails(fragment: Fragment) {
        mFirestore.collection(Constanse.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(fragment.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    fragment.requireActivity().getSharedPreferences(
                        Constanse.MYSHOPPAL_PREFERENCE,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                // Key:Value logged_in_username:ivan zolo

                editor.putString(
                    Constanse.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (fragment) {
                    is LoginFragment -> {
                        fragment.userLoggedInSuccessa(user)
                    }

                }

            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is LoginFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun getUserFragmentDetails(fragment: NotificationsFragment) {
        mFirestore.collection(Constanse.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(fragment.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!

                val sharedPreferences = fragment.requireContext().getSharedPreferences(
                    Constanse.MYSHOPPAL_PREFERENCE,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constanse.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                fragment.userDetailsSuccess(user)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting user details."
                )
            }
    }
    fun updateUserProfileData(fragment: Fragment, userHashMap: HashMap<String, Any>) {
        mFirestore.collection(Constanse.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (fragment) {
                    is UserProfileFragment -> {
                        fragment.userProfileUpdateSuccesss()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is UserProfileFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(fragment: Fragment, imageFileURI: Uri?) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constanse.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." +
                    Constanse.getFileExtension(
                        fragment.requireActivity(),
                        imageFileURI
                    )
        )
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Download image URL", uri.toString())
                        when (fragment) {
                            is UserProfileFragment -> {
                                fragment.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                when (fragment) {
                    is UserProfileFragment -> {
                        fragment.hideProgressDialog()
                    }
                }

                Log.e(
                    fragment.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }
}