package com.sun.moviedb.screen.favorite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sun.moviedb.data.model.Movie
import com.sun.moviedb.databinding.ItemFavoriteMovieBinding
import com.sun.moviedb.utils.base.BaseAdapter

class FavoriteMovieAdapter(private val onMovieClick: ((Movie) -> Unit)? = null) :
    BaseAdapter<Movie, FavoriteMovieAdapter.FavoriteMovieViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieViewHolder {
        val binding =
            ItemFavoriteMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteMovieViewHolder, position: Int) {
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

    inner class FavoriteMovieViewHolder(private val binding: ItemFavoriteMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) = with(binding) {
            Glide.with(ivThumbnail.context)
                .load(movie.thumbUrl.ifEmpty { movie.posterUrl })
                .centerCrop()
                .into(ivThumbnail)

            tvType.setTextOrGone(movie.type)
            tvTitle.text = movie.name.ifBlank { movie.originName }
            tvYear.setTextOrGone(if (movie.year > 0) movie.year.toString() else null)
            tvCountry.setTextOrGone(movie.country.firstOrNull()?.name)
            tvQuality.setTextOrGone(movie.quality)
            tvCategory.setTextOrGone(movie.category.firstOrNull()?.name)

            root.setOnClickListener { onMovieClick?.invoke(movie) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem == newItem
        }
    }
}