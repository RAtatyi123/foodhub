package com.example.myapplication.activities.ui.notifications


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.R
import com.example.myapplication.activities.BaseFragment
import com.example.myapplication.activities.MyOrderActivity
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.example.myapplication.firestore.FirestoreClass
import com.example.myapplication.fragments.LoginFragment
import com.example.myapplication.fragments.RegisterFragment
import com.example.myapplication.fragments.UserProfileFragment
import com.example.myapplication.model.User
import com.example.myapplication.utils.Constanse
import com.example.myapplication.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.fragment.findNavController

class NotificationsFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mUserDetails: User

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationsViewModel: NotificationsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val tvEdit = view?.findViewById<View>(R.id.tv_edit)
        tvEdit?.setOnClickListener {
            val navController = Navigation.findNavController(tvEdit)

        }


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupActionBar()
        binding.tvEdit2.setOnClickListener(this)
        binding.tvEdit.setOnClickListener(this)
        binding.btnLogout.setOnClickListener(this)

        getUserFragmentDetails()

        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        notificationsViewModel.text.observe(viewLifecycleOwner) { text ->
            // Здесь вы можете использовать значение "text" для обновления пользовательского интерфейса
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupActionBar() {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbarProfileFragment)

        val actionBar = activity.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarProfileFragment.setNavigationOnClickListener { activity.onBackPressed() }
    }

    private fun getUserFragmentDetails() {
       showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass(requireContext()).getUserFragmentDetails(this)
    }

    fun userDetailsSuccess(user: User) {

        mUserDetails = user

        hideProgressDialog()

        GlideLoader(requireContext()).loadUserPicture(user.image, binding.ivUserPhoto)
        binding.tvName.text = "${user.firstName} ${user.lastName}"
        binding.tvGender.text = user.gender
        binding.tvEmail.text = user.email
        binding.tvMobileNumber.text = "${user.mobile}"
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.tv_edit -> {
                    val navController = findNavController()
                    val bundle = Bundle()
                    bundle.putParcelable(Constanse.EXTRA_USER_DETAILS, mUserDetails)
                    navController.navigate(R.id.action_navigation_notifications_to_userProfileFragment2, bundle)
                }

                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val activity = requireActivity() as com.example.myapplication.activities.MainActivity
                    activity.navigateToLoginFragment()
                }
                R.id.tv_edit2 -> {
                    val intent = Intent(requireContext(), MyOrderActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}