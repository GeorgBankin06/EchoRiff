package com.echoriff.echoriff.radio.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.RadioItemBinding
import com.echoriff.echoriff.radio.domain.model.Radio

class RadiosAdapter(
    private val radios: List<Radio>,
    private val onItemClick: (Radio) -> Unit,
    private val onMoreClick: (Radio, Int) -> Unit
) : RecyclerView.Adapter<RadiosAdapter.RadioViewHolder>() {

    class RadioViewHolder(private val binding: RadioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(radio: Radio, onItemClick: (Radio) -> Unit, onMoreClick: (Radio, Int) -> Unit) {
            binding.radioName.text = radio.title
            binding.radioDescription.text = radio.intro
            Glide.with(binding.radioName.context)
                .load(radio.coverArtUrl)
                .placeholder(R.drawable.border_with_radius)
                .into(binding.radioIv)

            binding.menuIcon.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.inflate(R.menu.popup_menu)

                popup.setOnMenuItemClickListener { menuItem ->
                    onMoreClick(radio, menuItem.itemId)
                    true
                }

                popup.show()
            }

            itemView.setOnClickListener { onItemClick(radio) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val binding = RadioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RadioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        holder.bind(radios[position], onItemClick, onMoreClick)
    }

    override fun getItemCount(): Int = radios.size
}