package com.echoriff.echoriff

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.echoriff.echoriff.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHintAndCornerAnimation(binding.etName, "First Name")
        setupHintAndCornerAnimation(binding.etLastname, "Last Name")
        setupHintAndCornerAnimation(binding.etEmail, "Enter your email address...")
        setupHintAndCornerAnimation(binding.etPassword, "Enter your password")
        setupHintAndCornerAnimation(binding.etConfirmPass, "Confirm your password")

        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupHintAndCornerAnimation(editText: EditText, hint: String) {
        val transparentColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        val hintColor = ContextCompat.getColor(requireContext(), R.color.white)

        // Set default background for the EditText
        editText.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners)

        editText.setOnFocusChangeListener { view, hasFocus ->
            val field = view as EditText

            if (hasFocus) {
                // Apply fade-out animation and clear hint
                fadeOutHint(field, hintColor, transparentColor) {
                    field.hint = "" // Clear hint when fade-out completes
                }
                // Animate corner radius to larger value
                animateCornerRadius(field, startRadius = 8f.dpToPx(), endRadius = 24f.dpToPx())
            } else if (field.text.isEmpty()) {
                // Apply fade-in animation and reset hint
                field.hint = hint
                fadeInHint(field, transparentColor, hintColor)
                // Animate corner radius back to original value
                animateCornerRadius(field, startRadius = 24f.dpToPx(), endRadius = 8f.dpToPx())
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
            override fun onAnimationEnd(animation: Animator) { onComplete() }
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

    private fun animateCornerRadius(editText: EditText, startRadius: Float, endRadius: Float, duration: Long = 300) {
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
                tilName.error = "First Name is required"
                isValid = false
            } else {
                tilName.error = null
            }

            if (lastName.isEmpty()) {
                tilLastname.error = "Last Name is required"
                isValid = false
            } else {
                tilLastname.error = null
            }

            // Validate Email
            if (email.isEmpty()) {
                tilEmail.error = "Email is required"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Invalid email format"
                isValid = false
            } else {
                tilEmail.error = null
            }

            // Validate Password
            if (password.isEmpty()) {
                tilPassword.error = "Password is required"
                isValid = false
            } else if (password.length < 8) {
                tilPassword.error = "Password must be at least 8 characters long"
                isValid = false
            } else if (!password.matches(".*[!@#\$%^&*()].*".toRegex())) {
                tilPassword.error = "Password must contain at least one special character (!@#\$%^&*)"
                isValid = false
            } else {
                tilPassword.error = null
            }

            // Validate Confirm Password
            if (confirmPassword.isEmpty()) {
                tilConfirmPass.error = "Please confirm your password"
                isValid = false
            } else if (password != confirmPassword) {
                tilConfirmPass.error = "Passwords do not match"
                tvPasswordError.visibility = View.VISIBLE
                isValid = false
            } else {
                tilConfirmPass.error = null
                tvPasswordError.visibility = View.GONE
            }

            return isValid
        }
    }
}
