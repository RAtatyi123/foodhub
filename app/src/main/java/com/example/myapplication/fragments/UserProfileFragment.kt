package com.example.myapplication.fragments

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.ProgressDialog
import android.app.appsearch.SetSchemaRequest.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.activities.BaseFragment
import com.example.myapplication.databinding.FragmentUserProfileBinding
import com.example.myapplication.firestore.FirestoreClass
import com.example.myapplication.model.User
import com.example.myapplication.utils.Constanse
import com.example.myapplication.utils.GlideLoader
import java.io.IOException


class UserProfileFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""
    private lateinit var binding: FragmentUserProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        if (arguments != null && requireArguments().containsKey(Constanse.EXTRA_USER_DETAILS)) {
            mUserDetails = requireArguments().getParcelable(Constanse.EXTRA_USER_DETAILS)!!
        }

        binding.etFirstname.setText(mUserDetails.firstName)
        binding.etLastName.setText(mUserDetails.lastName)
        binding.etEmail.isEnabled = false
        binding.etEmail.setText(mUserDetails.email)

        if (mUserDetails.profileCompleted == 0) {
            binding.tvTitle.text = resources.getString(R.string.title_complete_profile)
            binding.etFirstname.isEnabled = false
            binding.etLastName.isEnabled = false
        } else {
            setupActionBar()
            binding.tvTitle.text = resources.getString(R.string.title_complete_profile)
            GlideLoader(requireContext()).loadUserPicture(mUserDetails.image, binding.ivUserPhoto)

            binding.etEmail.isEnabled = false
            binding.etEmail.setText(mUserDetails.email)

            if (mUserDetails.mobile != 0L) {
                binding.etMobileNumber.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constanse.MALE) {
                binding.rbMale.isChecked = true
            } else {
                binding.rbFemale.isChecked = true
            }
        }

        binding.ivUserPhoto.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)

        return binding.root
    }

    private fun setupActionBar() {
        // Код для настройки панели действий (action bar)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        showErrorSnackBar("You already have the storage permission.", false)
                        Constanse.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constanse.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_submit -> {
                    if (validateUserProfileDetails()) {
                        //showProgressDialog(resources.getString(R.string.please_wait))

                        if (mSelectedImageFileUri != null) {
                            FirestoreClass(requireContext()).uploadImageToCloudStorage(
                                this,
                                mSelectedImageFileUri
                            )
                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()
        val firstName = binding.etFirstname.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constanse.FIRST_NAME] = firstName
        }
        val lastName = binding.etLastName.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constanse.LAST_NAME] = lastName
        }
        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' ' }

        val gender = if (binding.rbMale.isChecked) {
            Constanse.MALE
        } else {
            Constanse.FEMALE
        }
        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constanse.IMAGE] = mUserProfileImageURL
        }
        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constanse.MOBILE] = mobileNumber.toLong()
        }
        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constanse.GENDER] = gender
        }
        userHashMap[Constanse.COMPLETE_PROFILE] = 1

        FirestoreClass(requireContext()).updateUserProfileData(this, userHashMap)
    }

    fun userProfileUpdateSuccesss() {
        hideProgressDialog()

        Toast.makeText(
            requireContext(),
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        val navController = findNavController()
        navController.navigate(R.id.action_userProfileFragment_to_dashboardActivity)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constanse.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constanse.showImageChooser(this)
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constanse.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(requireContext()).loadUserPicture(
                            mSelectedImageFileUri!!,
                            binding.ivUserPhoto
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.image_selection_fail),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    override fun showProgressDialog(message: String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    override fun hideProgressDialog() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.dismiss()
    }

    fun imageUploadSuccess(imageURL: String) {
        //hideProgressdialog()

        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

}