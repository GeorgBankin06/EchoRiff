package com.echoriff.echoriff.login.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.Constants
import com.echoriff.echoriff.common.domain.NetworkUtils
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.common.presentation.BaseFragment
import com.echoriff.echoriff.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashFragment : BaseFragment() {

    lateinit var binding: FragmentSplashBinding
    private val userPreferences: UserPreferences by inject()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            if (NetworkUtils(requireContext()).isNetworkAvailable(requireContext())) {
                checkUserRoleAndNavigate(currentUser.uid)
            } else {
                navigateBasedOnCachedRole()
            }
        } else {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(
                    R.id.splashFragment,
                    inclusive = true
                ).setExitAnim(R.anim.slide_out_2).setEnterAnim(R.anim.slide_in_1)
                .setPopEnterAnim(R.anim.slide_in_exit).setPopExitAnim(R.anim.slide_out_exit).build()
            findNavController().navigate(
                R.id.action_splashFragment_to_welcomeFragment,
                null,
                navOptions
            )
        }
    }

    private fun checkUserRoleAndNavigate(userId: String) {
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
                        navigateToRadiosActivity()
                    }
                } else {
                    navigateToLogin()
                }
            }.addOnFailureListener {
                navigateToLogin()
            }
    }

    private fun navigateBasedOnCachedRole() {
        lifecycleScope.launch {
            val cachedRole = userPreferences.userRole.first()
            if (cachedRole != null) {
                navigateToRadiosActivity()
            } else {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(
                R.id.splashFragment,
                inclusive = true
            )
            .build()
        findNavController().navigate(
            R.id.action_splashFragment_to_welcomeFragment,
            null,
            navOptions
        )
    }
}
