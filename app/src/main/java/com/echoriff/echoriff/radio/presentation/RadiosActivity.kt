package com.echoriff.echoriff.radio.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.ActivityRadiosBinding

class RadiosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRadiosBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRadiosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the navigation controller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.radio_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.main_nav_graph)
        binding.bottomNavigationView.setupWithNavController(navController)
        setupBottomNav()
    }

    private fun setupBottomNav() {
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_radios -> {
                   navController.navigate(R.id.radiosFragment)
                    true
                }

                R.id.nav_favorites -> {
                    navController.navigate(R.id.favoritesFragment)
                    true
                }

                R.id.nav_profile -> {
//                    navController.navigate(R.id.profileFragment)
                    true
                }

                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.radio_nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}