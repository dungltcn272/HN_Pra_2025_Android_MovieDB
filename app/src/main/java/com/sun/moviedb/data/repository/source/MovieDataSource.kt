package com.sun.moviedb.data.repository.source

import com.sun.moviedb.data.model.Category
import com.sun.moviedb.data.model.Country
import com.sun.moviedb.data.model.Movie
import com.sun.moviedb.data.repository.source.firebase.entity.MovieFirebaseEntity
import com.sun.moviedb.data.repository.source.remote.NetworkResult
import com.sun.moviedb.data.repository.source.remote.dto.MovieDetailResponse
import com.sun.moviedb.data.repository.source.remote.dto.MovieListResponse
import java.util.concurrent.Future

interface MovieDataSource {

    interface Local {
        // Favorite movies
        fun insertFavorite(movie: Movie)
        fun deleteFavorite(movieId: String)
        fun getFavorites(callback: (List<Movie>) -> Unit)
        fun getFavoriteById(movieId: String, callback: (Movie?) -> Unit)
        fun clearFavorites()

        // Search history
        fun insertSearchHistory(keyword: String)
        fun getSearchHistory(callback: (List<String>) -> Unit)
        fun clearSearchHistory()
    }

    interface Remote {
        fun getDetailMovie(
            slug: String,
            callback: (NetworkResult<MovieDetailResponse>) -> Unit,
        ): Future<*>

        fun getNewestMovie(
            page: Int,
            callback: (NetworkResult<MovieListResponse>) -> Unit
        ): Future<*>

        fun getSeriesMovie(
            typeList: String,
            page: Int,
            limit: Int,
            callback: (NetworkResult<MovieListResponse>) -> Unit
        ): Future<*>


        fun getFilterMovie(
            typeList: String,
            page: Int,
            limit: Int,
            sortField: String = "_id",
            sortType: String = "asc",
            sortLang: String? = null,
            country: String? = null,
            year: String? = null,
            callback: (NetworkResult<MovieListResponse>) -> Unit
        ): Future<*>

        fun searchMovie(
            keyword: String,
            page: Int,
            limit: Int,
            sortField: String = "_id",
            sortType: String = "asc",
            sortLang: String? = null,
            category: String? = null,
            country: String? = null,
            year: String? = null,
            callback: (NetworkResult<MovieListResponse>) -> Unit
        ): Future<*>

        fun getCategories(callback: (NetworkResult<List<Category>>) -> Unit): Future<*>

        fun getCountries(callback: (NetworkResult<List<Country>>) -> Unit): Future<*>
    }

    interface Firebase {
        fun addFavoriteMovieToFirebase(
            userId: String,
            movie: MovieFirebaseEntity,
            onComplete: (Boolean) -> Unit
        )

        fun removeFavoriteMovieFromFirebase(
            userId: String,
            movieId: String,
            onComplete: (Boolean) -> Unit
        )

        fun getFavoriteMoviesFromFirebase(
            userId: String,
            onResult: (List<MovieFirebaseEntity>) -> Unit
        )

        fun addSearchKeywordToFirebase(
            userId: String,
            keyword: String,
            onComplete: (Boolean) -> Unit
        )

        fun getRecentSearchKeywordsFromFirebase(
            userId: String,
            limit: Long = 10,
            onResult: (List<String>) -> Unit
        )
    }
}
