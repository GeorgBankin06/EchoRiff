package com.echoriff.echoriff.login.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.admin.AdminFragment
import com.echoriff.echoriff.databinding.FragmentWelcomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WelcomeFragment : Fragment() {

    lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(layoutInflater)
        val window = requireActivity().window

        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBar)
        window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.btnLogin.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_1)
                .setExitAnim(R.anim.slide_out_2)
                .setPopEnterAnim(R.anim.slide_in_exit)
                .setPopExitAnim(R.anim.slide_out_exit)
                .build()
            findNavController().navigate(
                R.id.action_welcomeFragment_to_loginFragment,
                null,
                navOptions
            )
        }

        binding.btnCreateAccount.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_1)
                .setExitAnim(R.anim.slide_out_2)
                .setPopEnterAnim(R.anim.slide_in_exit)
                .setPopExitAnim(R.anim.slide_out_exit)
                .build()
            findNavController().navigate(
                R.id.action_welcomeFragment_to_registerFragment,
                null,
                navOptions
            )
        }
    }


}
