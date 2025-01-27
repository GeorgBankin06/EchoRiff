package com.echoriff.echoriff.radio.presentation

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.FragmentRadioPlayerBinding
import com.echoriff.echoriff.radio.domain.RadioState
import com.echoriff.echoriff.radio.domain.SongState
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSongName.isSelected = true
        binding.tvArtist.isSelected = true

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

        binding.btnFavoriteRadio.setOnClickListener {
            playerModel.likeRadio(playerModel.nowPlayingRadio.value)
        }

        binding.btnFavoriteSong.setOnClickListener {
            playerModel.likeSong(playerModel.nowPlayingInfo.value)
        }
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
                    playerModel.likeRadio.collect { state ->
                        when (state) {
                            is RadioState.Loading -> {

                            }

                            is RadioState.Success -> {
                                Toast.makeText(
                                    requireContext(),
                                    state.messageSuccess,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is RadioState.Failure -> {
                                Toast.makeText(
                                    requireContext(),
                                    state.messageError,
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("TAGGY", state.messageError)
                            }
                        }
                    }
                }
                launch {
                    playerModel.likeSong.collect { state ->
                        when (state) {
                            is SongState.Loading -> {

                            }

                            is SongState.Success -> {
                                Toast.makeText(
                                    requireContext(),
                                    state.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is SongState.Failure -> {
                                Toast.makeText(
                                    requireContext(),
                                    state.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

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

            val currentBackground = binding.playerBackgroundView.background
            if (currentBackground is GradientDrawable) {
                // Animate the gradient transition by interpolating the colors
                val oldColors = (currentBackground as GradientDrawable).colors
                if (oldColors != null && oldColors.size == 2) {
                    val fromColor1 = oldColors[0]
                    val fromColor2 = oldColors[1]
                    val toColor1 = vibrantColor
                    val toColor2 = mutedColor

                    // Create a ValueAnimator for the entire gradient colors
                    val gradientAnimator =
                        ValueAnimator.ofObject(ArgbEvaluator(), fromColor1, toColor1)
                    gradientAnimator.duration = 1000
                    gradientAnimator.addUpdateListener { animator ->
                        val animatedColor1 = animator.animatedValue as Int
                        // Apply the animated colors to the gradient
                        gradientDrawable.colors = intArrayOf(animatedColor1, fromColor2)
                        binding.playerBackgroundView.background = gradientDrawable
                    }

                    // Create a second ValueAnimator for the second color
                    val gradientAnimator2 =
                        ValueAnimator.ofObject(ArgbEvaluator(), fromColor2, toColor2)
                    gradientAnimator2.duration = 1000
                    gradientAnimator2.addUpdateListener { animator ->
                        val animatedColor2 = animator.animatedValue as Int
                        // Apply the animated colors to the gradient
                        gradientDrawable.colors = intArrayOf(toColor1, animatedColor2)
                        binding.playerBackgroundView.background = gradientDrawable
                    }

                    // Start both animators to transition the gradient as a whole
                    gradientAnimator.start()
                    gradientAnimator2.start()
                }
            }

            // Set the new gradient as the background immediately (after animation starts)
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

    private fun captureMotion() {
        // Set the TransitionListener to capture state changes
//        binding.motion.setTransitionListener(object : MotionLayout.TransitionListener {
//            override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {
//                // This is called when the transition starts
//            }
//
//            override fun onTransitionChange(
//                layout: MotionLayout?,
//                startId: Int,
//                endId: Int,
//                progress: Float
//            ) {
//                // This is called when the transition is in progress
//                // You can capture if it's in max or min state based on progress
//                if (progress == 0f) {
//                    Toast.makeText(requireContext(), "max", Toast.LENGTH_SHORT).show()
//                } else if (progress == 1f) {
//                    Toast.makeText(requireContext(), "min", Toast.LENGTH_SHORT).show()
//                    // The layout is in the 'min' state (end constraint set)
//                }
//            }
//
//            override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
//                // This is called when the transition is completed
//                if (currentId == R.id.min) {
//                    // The layout has reached the 'min' state
//                    println("Transition completed: In min state")
//                } else if (currentId == R.id.max) {
//                    // The layout has reached the 'max' state
//                    println("Transition completed: In max state")
//                }
//            }
//
//            override fun onTransitionTrigger(
//                motionLayout: MotionLayout?,
//                triggerId: Int,
//                positive: Boolean,
//                progress: Float
//            ) {
//                TODO("Not yet implemented")
//            }
//        })
    }

    private fun applyBlurEffect(view: View, radius: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurEffect = RenderEffect.createBlurEffect(
                radius, // Horizontal blur radius
                radius, // Vertical blur radius
                Shader.TileMode.CLAMP // Prevents tiling at the edges
            )
            view.setRenderEffect(blurEffect)
        }
    }
}
