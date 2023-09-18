package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.activities.DashboardActivity
import android.os.Handler
import android.os.Looper
import com.example.myapplication.activities.MainActivity


class SplashFragment : Fragment() {
    private val splashTimeOut: Long = 2000 // Время отображения сплеш-экрана (2 секунды)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(requireContext(), DashboardActivity::class.java)
            intent.putExtra("openUserProfileFragment", true) // Указываем, что нужно открыть LoginFragment
            startActivity(intent)
            requireActivity().finish()
        }, splashTimeOut)
    }
}