package com.echoriff.echoriff.login.presentation

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.common.presentation.BaseFragment
import com.echoriff.echoriff.databinding.FragmentLoginBinding
import com.echoriff.echoriff.login.domain.LoginState
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment() {

    private val loginViewModel: LoginViewModel by viewModel()
    private val userPreferences: UserPreferences by inject()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        windowColors(R.color.statusBar, R.color.navBar)

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
        observeViewModel()

        binding.btnLogin.setOnClickListener {
            if (validateInputs()) {
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString()
                loginViewModel.login(email, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_registerFragment
            )
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(context, "Forgot", Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    loginViewModel.loginState.collect { state ->
                        when (state) {
                            is LoginState.Loading -> {
                                binding.progressIndicator.visibility = View.VISIBLE
                                binding.dimmerOverlay.visibility = View.VISIBLE
                            }

                            is LoginState.Success -> {
                                binding.progressIndicator.visibility = View.GONE
                                binding.dimmerOverlay.visibility = View.GONE

                                navigateToRadiosActivity()
                                userPreferences.saveUserRole(state.role)
                            }

                            is LoginState.Failure -> {
                                binding.progressIndicator.visibility = View.GONE
                                binding.dimmerOverlay.visibility = View.GONE
                                Toast.makeText(
                                    context,
                                    state.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            LoginState.Idle -> {}
                        }
                    }
                }
            }
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
