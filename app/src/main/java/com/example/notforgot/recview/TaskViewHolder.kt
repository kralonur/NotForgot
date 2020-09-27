package com.example.notforgot.recview

import androidx.recyclerview.widget.RecyclerView
import com.example.notforgot.databinding.ItemTaskBinding
import com.example.notforgot.model.TaskDomain

class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        task: TaskDomain,
        clickListener: TaskClickListener,
    ) {
        binding.task = task
        binding.clickListener = clickListener
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val checked = if (isChecked) 1 else 0
            if (task.task.done != checked)
                clickListener.onChecked(task.task)
        }
        binding.executePendingBindings()
    }
}