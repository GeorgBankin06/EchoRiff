package com.echoriff.echoriff.login.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.admin.AdminFragment
import com.echoriff.echoriff.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
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
            checkUserRoleAndNavigate(currentUser.uid)
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
        }
    }

    private fun checkUserRoleAndNavigate(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
            val role = document.getString("role")
            if (role == "admin") {
                val manager = parentFragmentManager.beginTransaction()
                manager.replace(R.id.nav_host_fragment, AdminFragment())
                manager.commit()
            } else {
                loadNavGraph(R.navigation.main_nav_graph)
            }
        }.addOnFailureListener {
            findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
        }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun loadNavGraph(graphId: Int) {
        parentFragmentManager.popBackStack(
            null,
            androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        val navHostFragment = NavHostFragment.create(graphId)

        val fragmentTransaction = fragmentManager?.beginTransaction()

        fragmentTransaction?.setCustomAnimations(
            R.anim.slide_in_1,  // Enter animation
            R.anim.slide_out_2,  // Exit animation
            R.anim.slide_in_exit,   // Pop enter (when coming back)
            R.anim.slide_out_exit  // Pop exit (when coming back)
        )

        fragmentTransaction
            ?.replace(R.id.nav_host_fragment, navHostFragment)
            ?.setPrimaryNavigationFragment(navHostFragment) // Set as the primary NavHostFragment
            ?.commit()
    }
}
