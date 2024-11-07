package com.echoriff.echoriff.radio.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.echoriff.echoriff.databinding.CategoryItemBinding
import com.echoriff.echoriff.radio.domain.model.CategoryDto

class CategoriesAdapter (
    private val categories: List<CategoryDto>,
    private val onItemClick: (CategoryDto) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryDto) {
            binding.categoryName.text = category.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])

        holder.itemView.setOnClickListener {
            onItemClick(categories[position])
        }

    }

    override fun getItemCount(): Int = categories.size
}
