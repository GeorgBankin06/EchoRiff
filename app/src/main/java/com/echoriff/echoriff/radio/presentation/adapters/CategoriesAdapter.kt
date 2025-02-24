package com.echoriff.echoriff.radio.presentation.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.CategoryItemBinding
import com.echoriff.echoriff.radio.domain.model.Category

class CategoriesAdapter(
    private val categories: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {
    private var selectedPosition = 0

    class CategoryViewHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, isSelected: Boolean) {
            with(binding) {
                categoryName.text = category.title
                if (isSelected) {
                    categoryName.setBackgroundResource(R.drawable.category_background_selected)
                    categoryName.textSize = 18f
                    categoryName.setTypeface(null, Typeface.BOLD)
                } else {
                    categoryName.setBackgroundResource(R.drawable.border_with_radius)
                    categoryName.textSize = 16f
                    categoryName.setTypeface(null, Typeface.NORMAL)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val isSelected = position == selectedPosition

        holder.bind(categories[position], isSelected)

        holder.itemView.setOnClickListener {
            onItemClick(categories[position])

            notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
        }

    }

    override fun getItemCount(): Int = categories.size
}
