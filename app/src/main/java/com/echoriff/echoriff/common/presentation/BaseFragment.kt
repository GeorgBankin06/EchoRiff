package com.echoriff.echoriff.common.presentation

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.echoriff.echoriff.R
import com.echoriff.echoriff.RadiosActivity
import com.google.android.material.textfield.TextInputLayout

open class BaseFragment : Fragment() {

    fun navigateToRadiosActivity() {
        val intent = Intent(requireContext(), RadiosActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    fun windowColors(statusBarColor: Int, navBarColor: Int) {
        val window = requireActivity().window

        window.statusBarColor = ContextCompat.getColor(requireContext(), statusBarColor)
        window.navigationBarColor = ContextCompat.getColor(requireContext(), navBarColor)
    }

    fun setupHintAndCornerAnimation(fragment: Fragment, editText: EditText, hint: String) {
        val transparentColor =
            ContextCompat.getColor(fragment.requireContext(), android.R.color.transparent)
        val hintColor = ContextCompat.getColor(fragment.requireContext(), R.color.white)

        // Set default background for the EditText
        editText.background =
            ContextCompat.getDrawable(fragment.requireContext(), R.drawable.rounded_corners)

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

    fun adjustStatusBarIconsBasedOnBackgroundColor(fragment: Fragment, backgroundColor: Int) {
        val window = fragment.requireActivity().window

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


    fun showEndIcon(fragment: Fragment, inputLayout: TextInputLayout, show: Boolean) {
        if (show) {
            // Set the end icon mode and drawable
            inputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
            inputLayout.setEndIconDrawable(R.drawable.ic_check) // Use your check icon
            inputLayout.setEndIconTintList(
                ContextCompat.getColorStateList(
                    fragment.requireContext(),
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
            inputLayout.setEndIconTintList(
                ContextCompat.getColorStateList(
                    fragment.requireContext(),
                    R.color.white
                )
            )
        }
    }

    fun showErrorWithAnimation(inputLayout: TextInputLayout, errorMessage: String) {
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