package com.example.securenotes.feat.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchResultRecyclerViewAdapter(
    private val results: List<String>,
    private val onResultClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val tvResult = LayoutInflater.from(parent.context).inflate(
            android.R.layout.simple_list_item_1, parent, false
        ) as TextView

        tvResult.setOnClickListener {
            onResultClick(tvResult.text.toString())
        }

        return object : RecyclerView.ViewHolder(tvResult) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tvResult = holder.itemView as TextView
        tvResult.text = results[position]
    }

    override fun getItemCount() = results.size
}
