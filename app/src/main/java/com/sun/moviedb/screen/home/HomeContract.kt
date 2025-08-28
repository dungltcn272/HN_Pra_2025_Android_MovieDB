package com.sun.moviedb.screen.home

import com.sun.moviedb.data.model.Item
import com.sun.moviedb.utils.base.BasePresenter
import com.sun.moviedb.utils.base.BaseView

interface HomeContract {
    interface HomeView : BaseView {
        fun showNewestMoviesLoading(isLoading: Boolean)
        fun showSeriesMoviesLoading(isLoading: Boolean)
        fun showNewestMovies(items: List<Item>)
        fun showSeriesMovies(items: List<Item>, currentPage: Int, totalPage: Int)
    }

    interface HomePresenter : BasePresenter<HomeView> {
        fun loadNewestMovies()
        fun loadSeriesMovies(series: String? = null, page: Int = 1)
        val seriesList: List<String>
        fun selectSeries(series: String)
        fun selectPage(page: Int)
    }
}
