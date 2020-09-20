package com.example.notforgot.util

import android.graphics.Color
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter


@BindingAdapter("bindColor")
fun AppCompatImageView.bindColor(color: String?) {
    color?.let {
        this.setBackgroundColor(Color.parseColor(it))
    }
}