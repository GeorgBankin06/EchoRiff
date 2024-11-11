package com.echoriff.echoriff.radio.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            } else {
                playerModel.play()
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
            }
        }
    }

    companion object {
        const val TAG = "PlayerFragment"

        fun newInstance(): PlayerFragment {
            val playScreenFragment = PlayerFragment()
            return playScreenFragment
        }
    }
}