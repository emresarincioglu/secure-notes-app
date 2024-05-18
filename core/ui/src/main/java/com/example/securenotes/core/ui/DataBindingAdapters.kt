package com.example.securenotes.core.ui

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("app:goneIf")
fun View.goneIf(condition: Boolean) {
    visibility = if (condition) View.GONE else View.VISIBLE
}