package com.echoriff.echoriff.favorite.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.echoriff.echoriff.databinding.FavoriteSongItemBinding
import com.echoriff.echoriff.radio.domain.Recording
import com.echoriff.echoriff.radio.domain.model.Song

class RecordsAdapter(
    private var records: List<Recording>,
    private val onRecordClick: (Recording) -> Unit,
    private val onButtonClick: (Recording) -> Unit
) :
    RecyclerView.Adapter<RecordsAdapter.RecordsViewHolder>() {

    class RecordsViewHolder(private val binding: FavoriteSongItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            record: Recording,
            onRecordClick: (Recording) -> Unit,
            onButtonClick: (Recording) -> Unit
        ) {
            binding.songName.text = record.fileName
            binding.songArtist.text = record.date
            binding.root.setOnClickListener { onRecordClick(record) }

            itemView.setOnClickListener { onRecordClick(record) }
            binding.btnDelete.setOnClickListener { onButtonClick(record) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        val binding =
            FavoriteSongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordsViewHolder(binding)
    }

    override fun getItemCount(): Int = records.size

    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        holder.bind(records[position], onRecordClick, onButtonClick)
    }

    fun removeItem(record: Recording){
        val index = records.indexOfFirst { it.filePath == record.filePath }
        if(index != -1){
            records = records.toMutableList().apply { removeAt(index) }
            notifyItemRemoved(index)
        }
    }
}