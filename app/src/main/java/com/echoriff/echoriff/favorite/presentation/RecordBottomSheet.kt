package com.echoriff.echoriff.favorite.presentation

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.presentation.BaseFragment
import com.echoriff.echoriff.databinding.BottomSheetRecordBinding
import com.echoriff.echoriff.radio.presentation.PlayerViewModel
import com.echoriff.echoriff.radio.service.RecordingService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.navigation.koinNavGraphViewModel

class RecordBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetRecordBinding
    private val playerModel: PlayerViewModel by koinNavGraphViewModel(R.id.main_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetRecordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bottom_sheet_background_record)
        setupHintAndCornerAnimation(this@RecordBottomSheet, binding.etSaveName, "Record Name")


        binding.btnSave.setOnClickListener {
            if (!binding.etSaveName.text.isNullOrEmpty()) {
                val name = binding.etSaveName.text.toString()
                val intent = Intent(requireContext(), RecordingService::class.java).apply {
                    action = RecordingService.ACTION_STOP
                    putExtra(RecordingService.EXTRA_RECORD_NAME, name)
                }
                requireContext().startService(intent)
                val notificationManager =
                    context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(1)
                playerModel.isRecording = false
                Toast.makeText(requireContext(), "Recording Stopped", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Enter recording name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
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
}