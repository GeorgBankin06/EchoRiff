package com.echoriff.echoriff.login.presentation

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputLayout

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

        binding.btnLogin.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                loadNavGraph(R.navigation.main_nav_graph)
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

    private fun loadNavGraph(graphId: Int) {
        parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val navHostFragment = NavHostFragment.create(graphId)

        val fragmentTransaction = parentFragmentManager.beginTransaction()

            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_1,  // Enter animation
                R.anim.slide_out_2,  // Exit animation
                R.anim.slide_in_exit,   // Pop enter (when coming back)
                R.anim.slide_out_exit  // Pop exit (when coming back)
            )

        fragmentTransaction
            .replace(R.id.nav_host_fragment, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment) // Set as the primary NavHostFragment
            .commit()
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
        setupHintAndCornerAnimation(binding.etEmail, "Enter your email address...")
        setupHintAndCornerAnimation(binding.etPassword, "Enter your password")
    }

    private fun setupHintAndCornerAnimation(editText: EditText, hint: String) {
        val transparentColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        val hintColor = ContextCompat.getColor(requireContext(), R.color.white)

        // Set default background for the EditText
        editText.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners)

        editText.setOnFocusChangeListener { view, hasFocus ->
            val field = view as EditText

//            field.animate().cancel()

            if (hasFocus) {
                // Immediately hide hint and start fade-out animation
                field.hint = ""
                fadeOutHint(field, hintColor, transparentColor)
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
                showEndIcon(tilEmail, true)
            }

            // Validate Password
            if (password.isEmpty()) {
                showErrorWithAnimation(tilPassword, "Password is required")
                isValid = false
            } else {
                tilPassword.error = null
                showEndIcon(tilPassword, true)
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
