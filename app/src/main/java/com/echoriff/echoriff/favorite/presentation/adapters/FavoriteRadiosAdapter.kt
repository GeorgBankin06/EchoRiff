package com.echoriff.echoriff.favorite.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.FavoriteRadioItemBinding
import com.echoriff.echoriff.radio.domain.model.Radio

class FavoriteRadiosAdapter(
    private var radios: List<Radio>,
    private val onRadioClick: (Radio) -> Unit
) : RecyclerView.Adapter<FavoriteRadiosAdapter.FavoriteRadiosViewHolder>() {

    class FavoriteRadiosViewHolder(private val binding: FavoriteRadioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(radio: Radio, onRadioClick: (Radio) -> Unit) {
            binding.radioName.text = radio.title
            binding.radioDescription.text = radio.intro
            Glide.with(binding.radioName.context)
                .load(radio.coverArtUrl)
                .placeholder(R.drawable.border_with_radius)
                .into(binding.radioIv)

            itemView.setOnClickListener { onRadioClick(radio) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRadiosViewHolder {
        val binding =
            FavoriteRadioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteRadiosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteRadiosViewHolder, position: Int) {
        holder.bind(radios[position], onRadioClick)
    }

    override fun getItemCount(): Int = radios.size

    fun updateRadios(newRadios: List<Radio>) {
        radios = newRadios
        notifyDataSetChanged()
    }
}