package com.echoriff.echoriff.register.presentation

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
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

        windowColors(R.color.statusBar, R.color.navBar)

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
                    scroll.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey
                        )
                    )
                    windowColors(R.color.grey, R.color.grey)

                    showProgressBar()

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
                            }

                            is RegisterState.Success -> {
                                binding.scroll.setBackgroundColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.black
                                    )
                                )

                                hideProgressBar()

                                windowColors(R.color.statusBar, R.color.navBar)

                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(
                                        R.id.auth_nav_graph,
                                        inclusive = true
                                    ).setExitAnim(R.anim.slide_out_2)
                                    .setEnterAnim(R.anim.slide_in_1)
                                    .setPopEnterAnim(R.anim.slide_in_exit)
                                    .setPopExitAnim(R.anim.slide_out_exit).build()
                                if (state.user.role == "admin") {
                                    findNavController().navigate(
                                        R.id.action_registerFragment_to_admin_nav_graph,
                                        null,
                                        navOptions
                                    )
                                } else {
                                    navigateToRadiosActivity()
                                }
                            }

                            is RegisterState.Failure -> {
                                windowColors(R.color.statusBar, R.color.navBar)

                                binding.progressIndicator.visibility = View.GONE
                                binding.scroll.setBackgroundColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.black
                                    )
                                )
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



    private fun showProgressBar() {
//        binding.scroll.visibility = View.VISIBLE
        binding.progressIndicator.visibility = View.VISIBLE
//
//        // Create fade-in animation for the background overlay
//        val fadeInBackground = AlphaAnimation(0.0f, 1.0f).apply {
//            duration = 500 // Duration of the fade-in animation
//            fillAfter = true // Keep the final state after the animation finishes
//        }

        // Create fade-in animation for the progress bar
        val fadeInProgressBar = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 200 // Duration of the fade-in animation
            fillAfter = true // Keep the final state after the animation finishes
        }

        // Start the animations
//        binding.scroll.startAnimation(fadeInBackground)
        binding.progressIndicator.startAnimation(fadeInProgressBar)
    }

    private fun hideProgressBar() {
//        // Create fade-out animation for the background overlay
//        val fadeOutBackground = AlphaAnimation(1.0f, 0.0f).apply {
//            duration = 500 // Duration of the fade-out animation
//            fillAfter = true // Keep the final state after the animation finishes
//        }

        // Create fade-out animation for the progress bar
        val fadeOutProgressBar = AlphaAnimation(1.0f, 0.0f).apply {
            duration = 200 // Duration of the fade-out animation
            fillAfter = true // Keep the final state after the animation finishes
        }

        // Start the animations
//        binding.scroll.startAnimation(fadeOutBackground)
        binding.progressIndicator.startAnimation(fadeOutProgressBar)

        // Hide the views after the animation ends
//        binding.scroll.visibility = View.GONE
        binding.progressIndicator.visibility = View.GONE
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

        /*binding.etName.addTextChangedListener {
            if (binding.etName.text.toString().isNotEmpty()) {
                binding.tilName.error = null
            }
        }

        binding.etLastname.addTextChangedListener {
            if (binding.etLastname.text.toString().isNotEmpty()) {
                binding.tilLastname.error = null
            }
        }

        binding.etEmail.addTextChangedListener {
            if (binding.etEmail.text.toString().isNotEmpty()) {
                binding.tilEmail.error = null
            }
        }*/

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
