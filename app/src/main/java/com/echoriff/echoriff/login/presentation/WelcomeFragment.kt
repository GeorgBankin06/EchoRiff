package com.echoriff.echoriff.login.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.presentation.BaseFragment
import com.echoriff.echoriff.databinding.FragmentWelcomeBinding

class WelcomeFragment : BaseFragment() {
    lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(layoutInflater)

        windowColors(R.color.transparent, R.color.black)

        adjustStatusBarIconsBasedOnBackgroundColor(
            this@WelcomeFragment, ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }

        binding.btnCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
        }
    }
}
