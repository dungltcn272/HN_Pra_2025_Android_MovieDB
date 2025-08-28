package com.sun.moviedb.screen.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sun.moviedb.data.model.Item
import com.sun.moviedb.databinding.ItemHomeMovieBinding
import com.sun.moviedb.utils.base.BaseAdapter

class SeriesMovieAdapter(
    private val onMovieClick: ((Item) -> Unit)? = null,
) : BaseAdapter<Item, SeriesMovieAdapter.MovieViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding =
            ItemHomeMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
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

    inner class MovieViewHolder(private val binding: ItemHomeMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) = with(binding) {
            Glide.with(imgThumbnail.context)
                .load(item.thumbUrl.ifEmpty { item.posterUrl })
                .centerCrop()
                .into(imgThumbnail)

            tvTimeRemaining.setTextOrGone(item.time)
            tvQuality.setTextOrGone(item.quality)
            tvEpisodeCurrent.setTextOrGone(item.episodeCurrent)
            tvCountry.setTextOrGone(item.country.firstOrNull()?.name)
            tvYear.setTextOrGone(if (item.year > 0) item.year.toString() else null)
            tvType.setTextOrGone(item.type)
            tvName.text = item.name.ifBlank { item.originName }

            root.setOnClickListener { onMovieClick?.invoke(item) }
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
