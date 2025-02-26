package com.echoriff.echoriff.favorite.presentation

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.echoriff.echoriff.databinding.BottomSheetSongOptionsBinding
import com.echoriff.echoriff.radio.domain.model.Song
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater as LayoutInflater1

class SongOptionsBottomSheet(private val song: Song) : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetSongOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater1, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetSongOptionsBinding.inflate(layoutInflater)
        binding.btnPlayYouTube.setOnClickListener {
            val query = "${song.songName} ${song.artist}"
            val youtubeIntent = Intent(Intent.ACTION_SEARCH).apply {
                setPackage("com.google.android.youtube")
                putExtra(SearchManager.QUERY, query)
            }

            try {
                startActivity(youtubeIntent)
                dismiss()
            } catch (e: ActivityNotFoundException) {
                dismiss()
                Toast.makeText(requireContext(), "YouTube is not installed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.btnPlaySpotify.setOnClickListener {
            val query = "spotify:search:${song.songName} ${song.artist}"
            val spotifyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(query))

            try {
                startActivity(spotifyIntent)
                dismiss()
            } catch (e: ActivityNotFoundException) {
                dismiss()
                Toast.makeText(requireContext(), "Spotify is not installed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
}