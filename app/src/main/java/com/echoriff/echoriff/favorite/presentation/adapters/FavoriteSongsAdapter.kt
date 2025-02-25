package com.echoriff.echoriff.favorite.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.echoriff.echoriff.databinding.FavoriteSongItemBinding
import com.echoriff.echoriff.radio.domain.model.Song

class FavoriteSongsAdapter(
    private var songs: List<Song>,
    private val onSongClick: (Song) -> Unit
) : RecyclerView.Adapter<FavoriteSongsAdapter.FavoriteSongsViewHolder>() {

    inner class FavoriteSongsViewHolder(private val binding: FavoriteSongItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song, onSongClick: (Song) -> Unit) {
            binding.songName.text = song.songName
            binding.songArtist.text = song.artist
            binding.root.setOnClickListener { onSongClick(song) }

            itemView.setOnClickListener { onSongClick(song) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteSongsViewHolder {
        val binding =
            FavoriteSongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteSongsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteSongsViewHolder, position: Int) {
        holder.bind(songs[position], onSongClick)
    }

    override fun getItemCount(): Int = songs.size

    fun updateSongs(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }
}