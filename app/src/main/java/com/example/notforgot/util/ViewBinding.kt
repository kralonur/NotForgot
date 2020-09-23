package com.example.notforgot.util

import android.graphics.Color
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.example.notforgot.R
import com.example.notforgot.model.items.priority.Priority
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView


@BindingAdapter("bindColor")
fun AppCompatImageView.bindColor(color: String?) {
    color?.let {
        this.setBackgroundColor(Color.parseColor(it))
    }
}

@BindingAdapter("bindDate")
fun MaterialTextView.bindDate(epoch: Long?) {
    epoch?.let {
        this.text = it.fromEpochToMs().toDateString()
    }
}

@BindingAdapter("bindDeadline")
fun MaterialTextView.bindDeadline(epoch: Long?) {
    epoch?.let {
        this.text = resources.getString(R.string.until, it.fromEpochToMs().toDateString())
    }
}

@BindingAdapter("bindPriority")
fun MaterialButton.bindPriority(priority: Priority?) {
    priority?.let {
        this.text = priority.name
        this.setBackgroundColor(Color.parseColor(priority.color))
    }
}