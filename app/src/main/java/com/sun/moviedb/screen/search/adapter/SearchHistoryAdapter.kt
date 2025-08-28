package com.sun.moviedb.screen.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sun.moviedb.R
import com.sun.moviedb.utils.base.BaseAdapter

class SearchHistoryAdapter(
    private val onItemClick: (String) -> Unit
) : BaseAdapter<String, SearchHistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvKeyword: TextView = itemView.findViewById(R.id.tvKeyword)
        fun bind(keyword: String) {
            tvKeyword.text = keyword
            itemView.setOnClickListener { onItemClick(keyword) }
        }
    }
}
