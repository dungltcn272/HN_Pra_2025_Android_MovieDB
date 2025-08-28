package com.sun.moviedb.screen.favorite

import com.sun.moviedb.data.repository.source.MovieRepository

class FavoriteMoviePresenter(private val repository: MovieRepository) : FavoriteMovieContract.FavoriteMoviePresenter {

    private var view : FavoriteMovieContract.FavoriteMovieView? = null

    override fun getFavoriteMovies() {
        view?.showLoading(true)
        repository.getFavorites { movies ->
            view?.showLoading(false)
            view?.showFavoriteMovies(movies)
        }
    }

    override fun attachView(view: FavoriteMovieContract.FavoriteMovieView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }
}