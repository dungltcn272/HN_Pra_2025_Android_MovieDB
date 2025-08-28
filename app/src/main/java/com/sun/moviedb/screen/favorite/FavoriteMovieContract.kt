package com.sun.moviedb.screen.favorite

import com.sun.moviedb.data.model.Movie
import com.sun.moviedb.utils.base.BasePresenter
import com.sun.moviedb.utils.base.BaseView

interface FavoriteMovieContract {
    interface FavoriteMovieView : BaseView {
        fun showFavoriteMovies(movies: List<Movie>)
    }

    interface FavoriteMoviePresenter : BasePresenter<FavoriteMovieView> {
        fun getFavoriteMovies()
    }
}
