package com.echoriff.echoriff.login.presentation

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.adjustStatusBarIconsBasedOnBackgroundColor
import com.echoriff.echoriff.common.loadNavGraph
import com.echoriff.echoriff.common.setupHintAndCornerAnimation
import com.echoriff.echoriff.common.showEndIcon
import com.echoriff.echoriff.common.showErrorWithAnimation
import com.echoriff.echoriff.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        val window = requireActivity().window

        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBar)
        window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.navBar)

        adjustStatusBarIconsBasedOnBackgroundColor(
            this@LoginFragment, ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCornerAnim()

        binding.btnLogin.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                loadNavGraph(this@LoginFragment, R.navigation.main_nav_graph)
            }
        }

        binding.tvLogin.setOnClickListener {
            Toast.makeText(context, "Sign Up", Toast.LENGTH_SHORT).show()
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(context, "Forgot", Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setupCornerAnim() {
        setupHintAndCornerAnimation(this@LoginFragment, binding.etEmail, "user@mail.com")
        setupHintAndCornerAnimation(this@LoginFragment, binding.etPassword, "Password")
    }

    private fun validateInputs(): Boolean {
        with(binding) {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            var isValid = true

            // Validate Email
            if (email.isEmpty()) {
                showErrorWithAnimation(tilEmail, "Email is required")
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showErrorWithAnimation(tilEmail, "Invalid email format")
                isValid = false
            } else {
                tilEmail.error = null
                showEndIcon(this@LoginFragment, tilEmail, true)
            }

            // Validate Password
            if (password.isEmpty()) {
                showErrorWithAnimation(tilPassword, "Password is required")
                isValid = false
            } else {
                tilPassword.error = null
                showEndIcon(this@LoginFragment, tilPassword, true)
            }

            return isValid
        }
    }
}
