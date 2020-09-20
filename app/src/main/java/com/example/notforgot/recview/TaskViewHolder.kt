package com.example.notforgot.recview

import androidx.recyclerview.widget.RecyclerView
import com.example.notforgot.databinding.ItemTaskBinding
import com.example.notforgot.model.items.task.Task

class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        task: Task,
        clickListener: TaskClickListener
    ) {
        binding.task = task
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }
}