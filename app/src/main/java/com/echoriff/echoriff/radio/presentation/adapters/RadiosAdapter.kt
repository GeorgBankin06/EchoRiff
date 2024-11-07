package com.echoriff.echoriff.radio.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.echoriff.echoriff.databinding.RadioItemBinding
import com.echoriff.echoriff.radio.domain.model.RadioDto

class RadiosAdapter(
    private val radios: List<RadioDto>,
    private val onItemClick: (RadioDto) -> Unit
) : RecyclerView.Adapter<RadiosAdapter.RadioViewHolder>() {

    class RadioViewHolder(private val binding: RadioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(radio: RadioDto) {
            binding.radioName.text = radio.title
            Glide.with(binding.radioName.context)
                .load(radio.coverArtUrl)
                .into(binding.radioIv)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val binding = RadioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RadioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        holder.bind(radios[position])

        holder.itemView.setOnClickListener {
            onItemClick(radios[position])
        }
    }

    override fun getItemCount(): Int = radios.size
}