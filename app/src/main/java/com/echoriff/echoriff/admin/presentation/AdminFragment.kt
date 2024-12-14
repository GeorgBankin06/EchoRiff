package com.echoriff.echoriff.admin.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.common.presentation.BaseFragment
import com.echoriff.echoriff.databinding.FragmentAdminBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AdminFragment : BaseFragment() {

    lateinit var binding: FragmentAdminBinding
    private val userPreferences: UserPreferences by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        lifecycleScope.launch {
            userPreferences.clearUserRole()
        }
        findNavController().navigate(R.id.auth_nav_graph)
    }
}
