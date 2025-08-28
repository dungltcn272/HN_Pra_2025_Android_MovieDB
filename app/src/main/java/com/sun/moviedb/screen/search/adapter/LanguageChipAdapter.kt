package com.sun.moviedb.screen.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sun.moviedb.R
import com.sun.moviedb.utils.LanguageMapper
import com.sun.moviedb.utils.base.BaseAdapter

class LanguageChipAdapter(
    private val onChipClick: (String?) -> Unit
) : BaseAdapter<String, LanguageChipAdapter.LanguageChipViewHolder>(DIFF_CALLBACK) {

    private var selected: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageChipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language_chip, parent, false)
        return LanguageChipViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageChipViewHolder, position: Int) {
        val language = getItem(position)
        holder.bind(language, language == selected)
        holder.itemView.setOnClickListener {
            handleChipClick(language)
        }
    }

    private fun handleChipClick(language: String) {
        val oldSelected = selected
        val newSelected = if (selected == language) null else language
        if (oldSelected != newSelected) {
            selected = newSelected
            oldSelected?.let {
                val oldIndex = items.indexOf(it)
                if (oldIndex != -1) notifyItemChanged(oldIndex)
            }
            newSelected?.let {
                val newIndex = items.indexOf(it)
                if (newIndex != -1) notifyItemChanged(newIndex)
            }
        }
        onChipClick(selected)
    }

    inner class LanguageChipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvChip: TextView = itemView.findViewById(R.id.tvLanguageChip)
        fun bind(language: String, isSelected: Boolean) {
            tvChip.text = LanguageMapper.getDisplayName(language)
            tvChip.isSelected = isSelected
            tvChip.setBackgroundResource(
                if (isSelected) R.drawable.bg_chip_selected else R.drawable.bg_chip
            )
            tvChip.alpha = if (isSelected) 1.0f else 0.7f
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }
}
