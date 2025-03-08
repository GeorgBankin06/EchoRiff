package com.echoriff.echoriff.favorite.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.echoriff.echoriff.databinding.FavoriteSongItemBinding
import com.echoriff.echoriff.radio.domain.model.Radio
import com.echoriff.echoriff.radio.domain.model.Song

class FavoriteSongsAdapter(
    private var songs: List<Song>,
    private val onSongClick: (Song) -> Unit,
    private val onButtonClick: (Song) -> Unit
) : RecyclerView.Adapter<FavoriteSongsAdapter.FavoriteSongsViewHolder>() {

     class FavoriteSongsViewHolder(private val binding: FavoriteSongItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song, onSongClick: (Song) -> Unit, onButtonClick: (Song) -> Unit) {
            binding.songName.text = song.songName
            binding.songArtist.text = song.artist
            binding.root.setOnClickListener { onSongClick(song) }

            itemView.setOnClickListener { onSongClick(song) }
            binding.btnDelete.setOnClickListener { onButtonClick(song) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteSongsViewHolder {
        val binding =
            FavoriteSongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteSongsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteSongsViewHolder, position: Int) {
        holder.bind(songs[position], onSongClick, onButtonClick)
    }

    fun removeItem(song: Song){
        val index = songs.indexOfFirst { it.songName == song.songName }
        if(index != -1){
            songs = songs.toMutableList().apply { removeAt(index) }

            notifyItemRemoved(index)
        }
    }

    override fun getItemCount(): Int = songs.size

    fun updateSongs(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }
}