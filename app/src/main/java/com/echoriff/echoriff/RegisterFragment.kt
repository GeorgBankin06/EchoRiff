package com.echoriff.echoriff

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.echoriff.echoriff.databinding.FragmentRegisterBinding
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        val window = requireActivity().window

        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)
        window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.black)

        adjustStatusBarIconsBasedOnBackgroundColor(
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

        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(context, "Google", Toast.LENGTH_SHORT).show()
        }

        binding.btnFacebook.setOnClickListener {
            Toast.makeText(context, "Facebook", Toast.LENGTH_SHORT).show()
        }
    }

    private fun adjustStatusBarIconsBasedOnBackgroundColor(backgroundColor: Int) {
        val window = requireActivity().window

        val luminance = calculateLuminance(backgroundColor)

        if (luminance < 0.5) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun calculateLuminance(color: Int): Float {
        val red = Color.red(color) / 255.0
        val green = Color.green(color) / 255.0
        val blue = Color.blue(color) / 255.0

        return (0.299 * red + 0.587 * green + 0.114 * blue).toFloat()
    }

    private fun setupCornerAnim() {
        setupHintAndCornerAnimation(binding.etName, "First Name")
        setupHintAndCornerAnimation(binding.etLastname, "Last Name")
        setupHintAndCornerAnimation(binding.etEmail, "Enter your email address...")
        setupHintAndCornerAnimation(binding.etPassword, "Enter your password")
        setupHintAndCornerAnimation(binding.etConfirmPass, "Confirm your password")
    }

    private fun passwordsListener() {
        binding.etPassword.addTextChangedListener {
            showEndIcon(binding.tilPassword, false)
//            showEndIcon(binding.tilConfirmPass, false)
            if (binding.etPassword.text.toString().isNotEmpty()) {
                binding.tilPassword.error = null
            }
        }

        binding.etConfirmPass.addTextChangedListener {
            showEndIcon(binding.tilConfirmPass, false)
//            showEndIcon(binding.tilPassword, false)
            if (binding.etConfirmPass.text.toString().isNotEmpty()) {
                binding.tilConfirmPass.error = null
            }
        }
    }

    private fun setupHintAndCornerAnimation(editText: EditText, hint: String) {
        val transparentColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        val hintColor = ContextCompat.getColor(requireContext(), R.color.white)

        // Set default background for the EditText
        editText.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners)

        editText.setOnFocusChangeListener { view, hasFocus ->
            val field = view as EditText

            if (hasFocus) {
                // Apply fade-out animation and clear hint
                fadeOutHint(field, hintColor, transparentColor) {
                    field.hint = "" // Clear hint when fade-out completes
                }
                // Animate corner radius to larger value if the EditText is empty
                animateCornerRadius(field, startRadius = 8f.dpToPx(), endRadius = 24f.dpToPx())
            } else {
                // Reset hint and animate corner radius back to small if text is empty
                if (field.text.isEmpty()) {
                    field.hint = hint
                    fadeInHint(field, transparentColor, hintColor)
                    animateCornerRadius(field, startRadius = 24f.dpToPx(), endRadius = 8f.dpToPx())
                }
                // Apply smaller corner radius if the EditText has text
                else {
                    animateCornerRadius(field, startRadius = 24f.dpToPx(), endRadius = 8f.dpToPx())
                }
            }

        }
    }

    private fun fadeOutHint(
        editText: EditText,
        fromColor: Int,
        toColor: Int,
        duration: Long = 500,
        onComplete: () -> Unit = {}
    ) {
        val colorAnimator = ValueAnimator.ofArgb(fromColor, toColor)
        colorAnimator.duration = duration
        colorAnimator.addUpdateListener { animator ->
            editText.setHintTextColor(animator.animatedValue as Int)
        }
        colorAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                onComplete()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        colorAnimator.start()
    }

    private fun fadeInHint(editText: EditText, fromColor: Int, toColor: Int, duration: Long = 500) {
        val colorAnimator = ValueAnimator.ofArgb(fromColor, toColor)
        colorAnimator.duration = duration
        colorAnimator.addUpdateListener { animator ->
            editText.setHintTextColor(animator.animatedValue as Int)
        }
        colorAnimator.start()
    }

    private fun animateCornerRadius(
        editText: EditText,
        startRadius: Float,
        endRadius: Float,
        duration: Long = 300
    ) {
        val background = editText.background.mutate() as GradientDrawable

        val animator = ValueAnimator.ofFloat(startRadius, endRadius)
        animator.duration = duration
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            background.cornerRadius = animatedValue
        }
        animator.start()
    }

    private fun Float.dpToPx(): Float {
        return this * Resources.getSystem().displayMetrics.density
    }

    private fun validateInputs(): Boolean {
        with(binding) {
            val firstName = etName.text.toString().trim()
            val lastName = etLastname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPass.text.toString()

            var isValid = true

            if (firstName.isEmpty()) {
                showErrorWithAnimation(tilName, "First Name is required")
                isValid = false
            } else {
                tilName.error = null

                showEndIcon(tilName, true)
            }

            if (lastName.isEmpty()) {
                showErrorWithAnimation(tilLastname, "Last Name is required")
                isValid = false
            } else {
                tilLastname.error = null
                showEndIcon(tilLastname, true)
            }

            // TODO: Add Email validation for already existing email (Firebase setup)
            // Validate Email
            if (email.isEmpty()) {
                showErrorWithAnimation(tilEmail, "Email is required")
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showErrorWithAnimation(tilEmail, "Invalid email format")
                isValid = false
            } else {
                tilEmail.error = null
                showEndIcon(tilEmail, true)
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
                showEndIcon(tilPassword, true)
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
                showEndIcon(tilConfirmPass, true)
//                tvPasswordError.visibility = View.GONE
            }

            return isValid
        }
    }

    private fun showEndIcon(inputLayout: TextInputLayout, show: Boolean) {
        if (show) {
            // Set the end icon mode and drawable
            inputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
            inputLayout.setEndIconDrawable(R.drawable.ic_check) // Use your check icon
            inputLayout.setEndIconTintList(
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.check
                )
            )

            // Apply fade-in animation
            val endIconView =
                inputLayout.findViewById<View>(com.google.android.material.R.id.text_input_end_icon)
            endIconView?.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(null)
            }

            inputLayout.isEndIconVisible = true
            inputLayout.clearFocus()
        } else {
            // Apply fade-out animation
            inputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
        }
    }

    private fun showErrorWithAnimation(inputLayout: TextInputLayout, errorMessage: String) {
        inputLayout.error = errorMessage
        if (!inputLayout.isVisible) {
            inputLayout.visibility = View.VISIBLE
            inputLayout.alpha = 0f
            inputLayout.animate().alpha(1f).duration = 500
        }

        // Shake animation for input field to draw attention
        inputLayout.startAnimation(shakeAnimation())
    }

    private fun shakeAnimation(): Animation {
        val shake = TranslateAnimation(0f, 10f, 0f, 0f)
        shake.duration = 300
        shake.repeatCount = 3
        shake.repeatMode = Animation.REVERSE
        return shake
    }
}
