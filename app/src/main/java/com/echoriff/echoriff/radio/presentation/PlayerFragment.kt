package com.echoriff.echoriff.radio.presentation

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.FragmentRadioPlayerBinding
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
            }
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
