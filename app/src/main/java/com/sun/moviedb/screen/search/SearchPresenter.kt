package com.sun.moviedb.screen.search

import com.sun.moviedb.data.model.Item
import com.sun.moviedb.data.repository.source.MovieRepository
import com.sun.moviedb.data.repository.source.remote.NetworkResult
import com.sun.moviedb.utils.LanguageMapper

class SearchPresenter(
    private val repository: MovieRepository
) : SearchContract.SearchPresenter {
    private var view: SearchContract.SearchView? = null
    private var keyword: String = ""
    private var selectedLanguage: String? = null
    private var hasMoreData: Boolean = true

    private val currentMovies = mutableListOf<Item>()
    private var lastLoadedPage: Int = 0
    private var isLoading: Boolean = false

    override fun attachView(view: SearchContract.SearchView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun searchMovie(keyword: String, sortLang: String?, page: Int) {
        if (isLoading) return
        isLoading = true
        if (page == 1) {
            currentMovies.clear()
            lastLoadedPage = 1
            hasMoreData = true
            this.keyword = keyword
            this.selectedLanguage = sortLang
            view?.showLoading(true)
        }

        repository.searchMovie(
            keyword = keyword,
            page = page,
            limit = DEFAULT_PAGE_SIZE,
            sortField = DEFAULT_SORT_FIELD,
            sortType = DEFAULT_SORT_TYPE,
            sortLang = sortLang
        ) { result ->
            isLoading = false
            view?.showLoading(false)
            when (result) {
                is NetworkResult.OnSuccess -> {
                    val items = result.data.items
                    currentMovies.addAll(items)
                    hasMoreData = page < result.data.pagination.totalPages
                    lastLoadedPage = page
                    if (currentMovies.isEmpty()) {
                        view?.showEmptyResult()
                    } else {
                        view?.showSearchResults(currentMovies.toList())
                    }
                }

                is NetworkResult.OnError -> {
                    view?.showError(result.message)
                }
            }
        }
    }

    override fun loadMoreResults() {
        if (hasMoreData && !isLoading) {
            searchMovie(keyword, selectedLanguage, lastLoadedPage + 1)
        }
    }

    override fun clearSearch() {
        currentMovies.clear()
        keyword = ""
        selectedLanguage = null
        hasMoreData = true
        lastLoadedPage = 0
        isLoading = false
        view?.showEmptyResult()
    }

    override fun loadLanguages() {
        val languages = LanguageMapper.getLanguageCodes()
        view?.showLanguages(languages)
    }

    override fun getSearchHistory() {
        repository.getSearchHistory { keywords ->
            val recentKeywords = if (keywords.size > 10) keywords.take(10) else keywords
            view?.showSearchHistory(recentKeywords)
        }
    }

    override fun insertSearchHistory(keyword: String) {
        repository.insertSearchHistory(keyword)
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
        private const val DEFAULT_SORT_FIELD = "createdAt"
        private const val DEFAULT_SORT_TYPE = "desc"
    }
}
