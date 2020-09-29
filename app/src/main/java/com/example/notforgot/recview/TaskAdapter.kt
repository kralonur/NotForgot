package com.example.notforgot.recview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notforgot.databinding.ItemTaskBinding
import com.example.notforgot.databinding.ItemTaskHeaderBinding
import com.example.notforgot.model.RecviewItem

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class TaskAdapter(private val clickListener: TaskClickListener) :
    ListAdapter<RecviewItem, RecyclerView.ViewHolder>(ListItemCallback()) {

    private class ListItemCallback : DiffUtil.ItemCallback<RecviewItem>() {
        override fun areItemsTheSame(oldItem: RecviewItem, newItem: RecviewItem): Boolean {
            if (oldItem.isTask != newItem.isTask)
                return false

            return if (oldItem.isTask && newItem.isTask)
                oldItem.task!!.task.id == newItem.task!!.task.id
            else oldItem.category!!.id == newItem.category!!.id
        }

        override fun areContentsTheSame(oldItem: RecviewItem, newItem: RecviewItem): Boolean {
            return oldItem.task == newItem.task
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> {
                val binding = ItemTaskBinding.inflate(inflater, parent, false)
                TaskViewHolder(binding)
            }
            ITEM_VIEW_TYPE_HEADER -> {
                val binding = ItemTaskHeaderBinding.inflate(inflater, parent, false)
                TaskHeaderViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TaskViewHolder -> holder.bind(getItem(position).task!!, clickListener)
            is TaskHeaderViewHolder -> holder.bind(getItem(position).category!!)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).isTask) {
            true -> ITEM_VIEW_TYPE_ITEM
            false -> ITEM_VIEW_TYPE_HEADER
        }
    }
}