package com.sun.moviedb.screen.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sun.moviedb.data.model.Item
import com.sun.moviedb.databinding.ItemNewestMoviePagerBinding
import com.sun.moviedb.utils.base.BaseAdapter

class NewestMoviePagerAdapter(
    private val onWatchNowClick: ((Item) -> Unit)? = null,
) : BaseAdapter<Item, NewestMoviePagerAdapter.MovieViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding =
            ItemNewestMoviePagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(private val binding: ItemNewestMoviePagerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            Glide.with(binding.imgPoster).load(item.posterUrl).into(binding.imgPoster)
            binding.txtTitle.text = item.name
            binding.txtGenres.text = item.category.joinToString(" · ") { it.name }
            binding.btnWatchNow.setOnClickListener { onWatchNowClick?.invoke(item) }
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
