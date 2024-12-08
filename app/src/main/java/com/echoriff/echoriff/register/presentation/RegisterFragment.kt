package com.echoriff.echoriff.register.presentation

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.presentation.BaseFragment
import com.echoriff.echoriff.databinding.FragmentRegisterBinding
import com.echoriff.echoriff.register.domain.RegisterState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : BaseFragment() {

    private val registerViewModel: RegisterViewModel by viewModel()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        val window = requireActivity().window

        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBar)
        window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.navBar)

        adjustStatusBarIconsBasedOnBackgroundColor(
            this@RegisterFragment,
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCornerAnim()

        passwordsListener()

        setupButtons()

        observeRegisterModel()
    }

    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            with(binding) {
                val firstName = etName.text.toString().trim()
                val lastName = etLastname.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()
                val confirmPassword = etConfirmPass.text.toString()

                if (validateInputs(firstName, lastName, email, password, confirmPassword)) {
                    registerViewModel.registerUser(firstName, lastName, email, password)
                }
            }
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(context, "Google", Toast.LENGTH_SHORT).show()
        }

        binding.btnFacebook.setOnClickListener {
            Toast.makeText(context, "Facebook", Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeRegisterModel() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    registerViewModel.registerState.collect { state ->
                        when (state) {
                            is RegisterState.Loading -> {
                                // Show loading state (e.g., show a progress bar)
//                              progressBar.visibility = View.VISIBLE
                            }

                            is RegisterState.Success -> {
                                // Hide loading and navigate based on user role
//                              progressBar.visibility = View.GONE
                                if (state.user.role == "admin") {
//                                    loadNavGraph(this@RegisterFragment ,R.navigation.admin_nav_graph)
                                    findNavController().navigate(R.id.action_registerFragment_to_admin_nav_graph)
                                } else {
//                                    loadNavGraph(this@RegisterFragment, R.navigation.main_nav_graph)
                                    findNavController().navigate(R.id.action_registerFragment_to_main_nav_graph)
                                }
                            }

                            is RegisterState.Failure -> {
                                // Hide loading and show error message
//                              progressBar.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    state.errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupCornerAnim() {
        setupHintAndCornerAnimation(this@RegisterFragment, binding.etName, "First Name")
        setupHintAndCornerAnimation(this@RegisterFragment, binding.etLastname, "Last Name")
        setupHintAndCornerAnimation(this@RegisterFragment, binding.etEmail, "user@mail.com")
        setupHintAndCornerAnimation(this@RegisterFragment, binding.etPassword, "Password")
        setupHintAndCornerAnimation(
            this@RegisterFragment,
            binding.etConfirmPass,
            "Confirm Password"
        )
    }

    private fun passwordsListener() {
        binding.etPassword.addTextChangedListener {
            showEndIcon(this@RegisterFragment, binding.tilPassword, false)
//            showEndIcon(binding.tilConfirmPass, false)
            if (binding.etPassword.text.toString().isNotEmpty()) {
                binding.tilPassword.error = null
            }
        }

        binding.etConfirmPass.addTextChangedListener {
            showEndIcon(this@RegisterFragment, binding.tilConfirmPass, false)
//            showEndIcon(binding.tilPassword, false)
            if (binding.etConfirmPass.text.toString().isNotEmpty()) {
                binding.tilConfirmPass.error = null
            }
        }
    }


    private fun validateInputs(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        with(binding) {
            var isValid = true

            if (firstName.isEmpty()) {
                showErrorWithAnimation(tilName, "First Name is required")
                isValid = false
            } else {
                tilName.error = null

                showEndIcon(this@RegisterFragment, tilName, true)
            }

            if (lastName.isEmpty()) {
                showErrorWithAnimation(tilLastname, "Last Name is required")
                isValid = false
            } else {
                tilLastname.error = null
                showEndIcon(this@RegisterFragment, tilLastname, true)
            }

            // Validate Email
            if (email.isEmpty()) {
                showErrorWithAnimation(tilEmail, "Email is required")
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showErrorWithAnimation(tilEmail, "Invalid email format")
                isValid = false
            } else {
                tilEmail.error = null
                showEndIcon(this@RegisterFragment, tilEmail, true)
            }

            // Validate Password
            if (password.isEmpty()) {
                showErrorWithAnimation(tilPassword, "Password is required")
                isValid = false
            } else if (password.length < 8) {
                showErrorWithAnimation(tilPassword, "Password must be at least 8 characters long")
                isValid = false
            } else if (!password.matches(".*[!@#\$%^&*()].*".toRegex())) {
                showErrorWithAnimation(
                    tilPassword,
                    "Password must contain at least one special character (!@#\$%^&*)"
                )
                isValid = false
            } else {
                tilPassword.error = null
                showEndIcon(this@RegisterFragment, tilPassword, true)
            }

            // Validate Confirm Password
            if (confirmPassword.isEmpty()) {
                showErrorWithAnimation(tilConfirmPass, "Please confirm your password")
                isValid = false
            } else if (password != confirmPassword) {
                showErrorWithAnimation(tilConfirmPass, "Passwords do not match")
//                tvPasswordError.visibility = View.VISIBLE
                isValid = false
            } else {
                tilConfirmPass.error = null
                showEndIcon(this@RegisterFragment, tilConfirmPass, true)
//                tvPasswordError.visibility = View.GONE
            }

            return isValid
        }
    }
}
