package com.sun.moviedb.screen.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sun.moviedb.data.model.Item
import com.sun.moviedb.databinding.ItemSearchBinding
import com.sun.moviedb.utils.base.BaseAdapter

class SearchAdapter(
    private val onItemClick: (Item) -> Unit = {}
) : BaseAdapter<Item, SearchAdapter.SearchViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun TextView.setTextOrGone(value: String?) {
        if (!value.isNullOrBlank()) {
            text = value
            visibility = View.VISIBLE
        } else {
            visibility = View.GONE
        }
    }

    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) = with(binding) {
            Glide.with(ivPoster.context)
                .load(item.posterUrl.ifEmpty { item.thumbUrl })
                .centerCrop()
                .into(ivPoster)

            tvQuality.setTextOrGone(item.quality)
            tvTime.setTextOrGone(item.time)
            tvCountry.setTextOrGone(item.country.firstOrNull()?.name)
            tvYear.setTextOrGone(if (item.year > 0) item.year.toString() else null)
            tvType.setTextOrGone(item.type)
            tvTitle.text = item.name.ifBlank { item.originName }
            tvCategory.setTextOrGone(item.category.firstOrNull()?.name)

            root.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
                oldItem == newItem
        }
    }
}
