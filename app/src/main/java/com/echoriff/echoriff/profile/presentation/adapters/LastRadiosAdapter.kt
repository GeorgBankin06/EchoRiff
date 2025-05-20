package com.echoriff.echoriff.profile.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.LastRadioItemBinding
import com.echoriff.echoriff.radio.domain.model.Radio

class LastRadiosAdapter(
    private val radios: List<Radio>,
    private val context: Context
) :
    RecyclerView.Adapter<LastRadiosAdapter.LastRadiosViewHolder>() {

    class LastRadiosViewHolder(private val binding: LastRadioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(radio: Radio, context: Context) {
            with(binding) {
                Glide.with(context)
                    .load(radio.coverArtUrl)
                    .placeholder(R.drawable.player_background)
                    .into(radioIv)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastRadiosViewHolder {
        val binding =
            LastRadioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LastRadiosViewHolder(binding)
    }

    override fun getItemCount(): Int = radios.size

    override fun onBindViewHolder(holder: LastRadiosViewHolder, position: Int) {
        holder.bind(radios[position], context)
    }

}