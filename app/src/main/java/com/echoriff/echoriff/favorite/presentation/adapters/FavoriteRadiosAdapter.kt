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
    private val onRadioClick: (Radio) -> Unit,
    private val onButtonClick: (Radio) -> Unit
) : RecyclerView.Adapter<FavoriteRadiosAdapter.FavoriteRadiosViewHolder>() {

    class FavoriteRadiosViewHolder(private val binding: FavoriteRadioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(radio: Radio, onRadioClick: (Radio) -> Unit, onButtonClick: (Radio) -> Unit) {
            binding.radioName.text = radio.title
            binding.radioDescription.text = radio.intro
            Glide.with(binding.radioName.context)
                .load(radio.coverArtUrl)
                .placeholder(R.drawable.border_with_radius)
                .into(binding.radioIv)

            itemView.setOnClickListener { onRadioClick(radio) }
            binding.btnDelete.setOnClickListener { onButtonClick(radio) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRadiosViewHolder {
        val binding =
            FavoriteRadioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteRadiosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteRadiosViewHolder, position: Int) {
        holder.bind(radios[position], onRadioClick, onButtonClick)
    }

    override fun getItemCount(): Int = radios.size

    fun removeItem(radio: Radio){
        val index = radios.indexOfFirst { it.title == radio.title }
        if(index != -1){
            radios = radios.toMutableList().apply { removeAt(index) }

            notifyItemRemoved(index)
        }
    }

    fun updateRadios(newRadios: List<Radio>) {
        radios = newRadios
        notifyDataSetChanged()
    }
}