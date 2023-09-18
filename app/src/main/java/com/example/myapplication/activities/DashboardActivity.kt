package com.example.myapplication.activities

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityDashboardBinding

class DashboardActivity : BaseActivity(){

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Переключение на фрагмент, связанный с первой вкладкой
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_myorder -> {
                    navController.navigate(R.id.navigation_myorder)
                    true
                }

                R.id.navigation_dashboard -> {
                    // Переключение на фрагмент, связанный с третьей вкладкой
                    navController.navigate(R.id.navigation_dashboard)
                    true
                }
                R.id.navigation_notifications -> {
                    // Переключение на фрагмент, связанный с третьей вкладкой
                    navController.navigate(R.id.navigation_notifications)
                    true
                }

                else -> false
            }
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }



}