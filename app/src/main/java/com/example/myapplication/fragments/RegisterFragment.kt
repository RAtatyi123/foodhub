package com.example.myapplication.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.activities.BaseFragment
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.example.myapplication.firestore.FirestoreClass
import com.example.myapplication.model.User
import com.example.myapplication.utils.Constanse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class RegisterFragment : BaseFragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        setOnClickListeners()
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar_register_activity)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setOnClickListeners() {
        binding.tvLogin.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFirstname.text.toString().trim()) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(binding.etLastName.text.toString().trim()) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(binding.etEmail.text.toString().trim()) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim()) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim()) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            binding.etPassword.text.toString().trim() != binding.etConfirmPassword.text.toString().trim() -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_password_and_confirm_password), true)
                false
            }

            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(getString(R.string.i_agree_to_the_terms_and_condition), true)
                false
            }

            else -> true
        }
    }

    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(getString(R.string.please_wait))

            val email: String = binding.etEmail.text.toString().trim()
            val password: String = binding.etPassword.text.toString().trim()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result?.user!!

                        val user = User(
                            id = firebaseUser.uid,
                            firstName = binding.etFirstname.text.toString().trim(),
                            lastName = binding.etLastName.text.toString().trim(),
                            email = binding.etEmail.text.toString().trim()
                        )

                        registerUserInFirestore(user)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception?.message.toString(), true)
                    }
                }
        }
    }

    private fun registerUserInFirestore(user: User) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection(Constanse.USERS)
        val document = usersCollection.document(user.id)

        document.set(user)
            .addOnSuccessListener {
                userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                hideProgressDialog()
                showErrorSnackBar(e.message.toString(), true)
            }
    }

     fun userRegistrationSuccess() {
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()
         findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}