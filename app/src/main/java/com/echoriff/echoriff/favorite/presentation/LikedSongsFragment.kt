package com.echoriff.echoriff.favorite.presentation

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.FragmentLikedSongsBinding
import com.echoriff.echoriff.favorite.domain.LikedSongsState
import com.echoriff.echoriff.favorite.presentation.adapters.FavoriteSongsAdapter
import com.echoriff.echoriff.radio.domain.model.Song
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikedSongsFragment : Fragment() {
    private lateinit var binding: FragmentLikedSongsBinding
    private val likedSongsViewModel: LikedSongsViewModel by viewModel()
    private lateinit var adapter: FavoriteSongsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedSongsBinding.inflate(layoutInflater)

        ViewCompat.setOnApplyWindowInsetsListener(binding.btnBack) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = systemBarsInsets.top
            view.layoutParams = layoutParams
            insets
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        observeLikedSongModel()
        setupSongsRV(view)
    }

    private fun observeLikedSongModel() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    likedSongsViewModel.likedSongsState.collect { state ->
                        when (state) {
                            is LikedSongsState.Loading -> {
                                binding.progressBar.visibility =
                                    View.VISIBLE
                                binding.likedSongsRv.visibility = View.GONE
                            }

                            is LikedSongsState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.likedSongsRv.visibility = View.VISIBLE
                                setupSongsAdapter(state.likedSongs)

                                val animation = AnimationUtils.loadLayoutAnimation(
                                    requireContext(),
                                    R.anim.layout_animation
                                )
                                binding.likedSongsRv.layoutAnimation = animation
                                binding.likedSongsRv.scheduleLayoutAnimation()
                                binding.tvRadiosNumber.text = "${state.likedSongs.size} songs"
                            }

                            is LikedSongsState.Failure -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    state.messageError,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupSongsRV(view: View) {
        binding.likedSongsRv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun setupSongsAdapter(songs: List<Song>) {
        adapter = FavoriteSongsAdapter(songs) { selectedSong ->
            val bottomSheet = SongOptionsBottomSheet(selectedSong)
            bottomSheet.show(parentFragmentManager, "SongOptionsBottomSheet")
        }
        binding.likedSongsRv.adapter = adapter
    }

}