package com.example.notforgot.recview

import androidx.recyclerview.widget.RecyclerView
import com.example.notforgot.databinding.ItemTaskHeaderBinding
import com.example.notforgot.model.db.items.DbCategory

class TaskHeaderViewHolder(private val binding: ItemTaskHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(category: DbCategory) {
        binding.category = category
        binding.executePendingBindings()
    }

}