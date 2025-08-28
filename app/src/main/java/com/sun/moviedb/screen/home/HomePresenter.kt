package com.sun.moviedb.screen.home

import com.sun.moviedb.data.repository.source.MovieRepository
import com.sun.moviedb.data.repository.source.remote.NetworkResult
import com.sun.moviedb.utils.SeriesMapper
import java.util.concurrent.Future

class HomePresenter(
    private val movieRepository: MovieRepository
) : HomeContract.HomePresenter {

    override val seriesList = SeriesMapper.getSeriesCodes()

    private var currentSeries: String = seriesList.first()
    private var currentPage: Int = 1
    private var view: HomeContract.HomeView? = null
    private var newestMovieFuture: Future<*>? = null
    private var seriesMovieFuture: Future<*>? = null

    override fun attachView(view: HomeContract.HomeView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
        newestMovieFuture?.cancel(true)
        seriesMovieFuture?.cancel(true)
    }

    override fun loadNewestMovies() {
        view?.showNewestMoviesLoading(true)
        newestMovieFuture = movieRepository.getNewestMovie(1) { result ->
            view?.showNewestMoviesLoading(false)
            when (result) {
                is NetworkResult.OnSuccess -> view?.showNewestMovies(
                    result.data.items.subList(0, 5)
                )

                is NetworkResult.OnError -> view?.showError(result.message)
            }
        }
    }

    override fun loadSeriesMovies(series: String?, page: Int) {
        val mSeries = series ?: currentSeries
        view?.showSeriesMoviesLoading(true)
        seriesMovieFuture =
            movieRepository.getSeriesMovie(mSeries, page, DEFAULT_PAGE_SIZE) { result ->
                view?.showSeriesMoviesLoading(false)
                when (result) {
                    is NetworkResult.OnSuccess -> view?.showSeriesMovies(
                        result.data.items,
                        result.data.pagination.currentPage,
                        result.data.pagination.totalPages
                    )

                    is NetworkResult.OnError -> view?.showError(result.message)
                }
            }
    }

    override fun selectSeries(series: String) {
        currentSeries = series
        currentPage = 1
        loadSeriesMovies(currentSeries, currentPage)
    }

    override fun selectPage(page: Int) {
        currentPage = page
        loadSeriesMovies(currentSeries, currentPage)
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }

}
