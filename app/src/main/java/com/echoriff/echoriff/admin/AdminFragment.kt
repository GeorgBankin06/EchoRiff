package com.echoriff.echoriff.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.UserPreferences
import com.echoriff.echoriff.databinding.FragmentAdminBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AdminFragment : Fragment() {

    lateinit var binding: FragmentAdminBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())

    }

    private fun logout() {
        lifecycleScope.launch {
            userPreferences.clearUserRole()
            FirebaseAuth.getInstance().signOut()
            // TODO Logout function
//            findNavController().navigate(R.id.action_currentFragment_to_loginFragment)
        }
    }
}
