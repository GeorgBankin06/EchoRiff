package com.echoriff.echoriff.radio.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.RadioItemBinding
import com.echoriff.echoriff.radio.domain.Radio

class RadiosAdapter(
    private val radios: List<Radio>,
    private val onItemClick: (Radio) -> Unit
) : RecyclerView.Adapter<RadiosAdapter.RadioViewHolder>() {

    class RadioViewHolder(private val binding: RadioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(radio: Radio, onItemClick: (Radio) -> Unit) {
            binding.radioName.text = radio.title
            binding.radioDescription.text = radio.intro
            Glide.with(binding.radioName.context)
                .load(radio.coverArtUrl)
                .placeholder(R.drawable.border_with_radius)
                .into(binding.radioIv)

            itemView.setOnClickListener { onItemClick(radio) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val binding = RadioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RadioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        holder.bind(radios[position], onItemClick)
    }

    override fun getItemCount(): Int = radios.size
}