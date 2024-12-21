package com.echoriff.echoriff.radio.presentation

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.FragmentRadioPlayerBinding
import com.echoriff.echoriff.radio.di.radioModule
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {

    private val playerModel: PlayerViewModel by navGraphViewModels(R.id.main_nav_graph)

    lateinit var binding: FragmentRadioPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRadioPlayerBinding.inflate(layoutInflater)
        ViewCompat.setOnApplyWindowInsetsListener(binding.collapseImage) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = systemBarsInsets.top + 20
            view.layoutParams = layoutParams
            insets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSongName.isSelected = true

        // Set the TransitionListener to capture state changes
        binding.motion.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {
                // This is called when the transition starts
            }

            override fun onTransitionChange(
                layout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                // This is called when the transition is in progress
                // You can capture if it's in max or min state based on progress
                if (progress == 0f) {
                    Toast.makeText(requireContext(), "max", Toast.LENGTH_SHORT).show()
                } else if (progress == 1f) {
                    Toast.makeText(requireContext(), "min", Toast.LENGTH_SHORT).show()
                    // The layout is in the 'min' state (end constraint set)
                }
            }

            override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
                // This is called when the transition is completed
                if (currentId == R.id.min) {
                    // The layout has reached the 'min' state
                    println("Transition completed: In min state")
                } else if (currentId == R.id.max) {
                    // The layout has reached the 'max' state
                    println("Transition completed: In max state")
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
                TODO("Not yet implemented")
            }
        })

        setupButtons()
        observePlayerModel()
    }

    private fun setupButtons() {
        binding.btnPlay.setOnClickListener {
            if (playerModel.isPlaying()) {
                playerModel.pause()
                binding.btnPlay.setImageResource(R.drawable.ic_play)
                updateTextColorBasedOnPlayerState(playerModel.isPlaying())
            } else {
                playerModel.play()
                binding.btnPlay.setImageResource(R.drawable.ic_pause)
                updateTextColorBasedOnPlayerState(playerModel.isPlaying())
            }
        }
        binding.btnNext.setOnClickListener { playerModel.playNext() }
        binding.btnPrev.setOnClickListener { playerModel.playPrev() }
    }

    private fun observePlayerModel() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    playerModel.nowPlayingRadio.collect { radio ->
                        Glide.with(binding.coverArtImage.context)
                            .load(radio?.coverArtUrl)
                            .placeholder(R.drawable.player_background)
                            .into(binding.coverArtImage)
                    }
                }
                launch {
                    playerModel.nowPlayingInfo.collect { (title, artist) ->
                        binding.tvSongName.text = title
                        binding.tvArtist.text = artist
                    }
                }
                launch {
                    playerModel.isPlayingState.collect { isPlaying ->
                        updatePlayButtonIcon(isPlaying)
                        updateTextColorBasedOnPlayerState(isPlaying)
                    }
                }
                launch {
                    playerModel.nowPlayingRadio.collect { radio ->
                        val radioImageUrl = radio?.coverArtUrl ?: ""
                        if (radioImageUrl.isEmpty()) {
//                            Toast.makeText(requireContext(), "Empty", Toast.LENGTH_SHORT).show()
                        } else {
                            updateRadioImageAndBackground(radioImageUrl)
                        }
                    }
                }
            }
        }
    }

    private fun updateRadioImageAndBackground(imageUrl: String) {
        Picasso.get()
            .load(imageUrl)
            .resize(200, 200) // Resize for efficient processing
            .centerCrop()
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    if (bitmap != null) {
                        setGradientBackground(bitmap)
                    }
                }

                override fun onBitmapFailed(
                    e: Exception?,
                    errorDrawable: android.graphics.drawable.Drawable?
                ) {
                    // Handle failure (optional placeholder)
                }

                override fun onPrepareLoad(placeHolderDrawable: android.graphics.drawable.Drawable?) {
                    // Handle loading state (optional placeholder)
                }
            })
    }

    private fun setGradientBackground(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            val vibrantColor = palette?.getVibrantColor(0xFF000000.toInt()) ?: 0xFF000000.toInt()
            val mutedColor = palette?.getMutedColor(0xFF000000.toInt()) ?: 0xFF000000.toInt()

            // Create a gradient drawable with the two most common colors
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(vibrantColor, mutedColor)
            )

            gradientDrawable.cornerRadii = floatArrayOf(
                100f, 100f, // top-left corner radius
                100f, 100f, // top-right corner radius
                100f, 100f,   // bottom-left corner (no radius)
                100f, 100f    // bottom-right corner (no radius)
            )

            val window = requireActivity().window

            // Set the gradient as the background of the player
            binding.playerBackgroundView.background = gradientDrawable
        }
    }

    private fun updatePlayButtonIcon(isPlaying: Boolean) {
        if (isPlaying) {
            binding.btnPlay.setImageResource(R.drawable.ic_pause)
        } else {
            binding.btnPlay.setImageResource(R.drawable.ic_play)
        }
    }

    private fun updateTextColorBasedOnPlayerState(isPlaying: Boolean) {
        val textView: TextView = binding.tvLive
        val fromColor: Int
        val toColor: Int

        if (isPlaying) {
            fromColor =
                ContextCompat.getColor(requireContext(), R.color.grey)
            toColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else {
            fromColor =
                ContextCompat.getColor(requireContext(), R.color.red)
            toColor =
                ContextCompat.getColor(requireContext(), R.color.grey)
        }

        animateTextColorChange(textView, fromColor, toColor, 300)
    }

    private fun animateTextColorChange(
        view: TextView,
        fromColor: Int,
        toColor: Int,
        duration: Long
    ) {
        val animator = ObjectAnimator.ofArgb(view, "textColor", fromColor, toColor)
        animator.duration = duration
        animator.start()
    }

    companion object {
        const val TAG = "PlayerFragment"

        fun newInstance(): PlayerFragment {
            val playScreenFragment = PlayerFragment()
            return playScreenFragment
        }
    }
}
