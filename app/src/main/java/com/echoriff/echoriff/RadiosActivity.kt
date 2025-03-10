package com.echoriff.echoriff

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.echoriff.echoriff.common.Constants
import com.echoriff.echoriff.common.domain.NetworkUtils
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.databinding.ActivityRadiosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RadiosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRadiosBinding
    private val userPreferences: UserPreferences by inject()
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRadiosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                checkUserRoleAndShowTabs(currentUser.uid)
            } else {
                navigateBasedOnCachedRole()
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            moveTaskToBack(true)
        }

        // Set up the navigation controller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.radio_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.main_nav_graph)
        binding.bottomNavigationView.setupWithNavController(navController)
        setupBottomNav()
    }

    // Check for role via Firestore
    private fun checkUserRoleAndShowTabs(userId: String) {
        val firestore = FirebaseFirestore.getInstance()

        firestore
            .collection(Constants.USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                if (role != null) {
                    lifecycleScope.launch {
                        userPreferences.saveUserRole(role)  // Save role locally
                        navigateToRoleBasedScreen(role)
                    }
                }
            }
    }
    // Check for role via UserPreferences
    private fun navigateBasedOnCachedRole() {
        lifecycleScope.launch {
            val cachedRole = userPreferences.userRole.first()
            if (cachedRole != null) {
                navigateToRoleBasedScreen(cachedRole)
            }
        }
    }

    private fun navigateToRoleBasedScreen(role: String) {
        if (role == "admin") {
            binding.bottomNavigationView.menu.clear()
            binding.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_admin)
        } else {
            binding.bottomNavigationView.menu.clear()
            binding.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu)
        }
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
                    navController.navigate(R.id.profileFragment)
                    true
                }

                R.id.nav_admin -> {
                    navController.navigate(R.id.adminFragment)
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