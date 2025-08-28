package com.sun.moviedb.screen.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.sun.moviedb.MyApp
import com.sun.moviedb.R
import com.sun.moviedb.data.model.Category
import com.sun.moviedb.data.model.Country
import com.sun.moviedb.data.model.Item
import com.sun.moviedb.databinding.FragmentFilterBinding
import com.sun.moviedb.screen.filter.adapter.FilterMovieAdapter
import com.sun.moviedb.utils.LanguageMapper
import com.sun.moviedb.utils.base.BaseFragment
import com.sun.moviedb.utils.navigation.AppNavigator
import com.sun.moviedb.utils.navigation.NavDestination

class FilterFragment : BaseFragment<FragmentFilterBinding>(), FilterContract.FilterView {

    private lateinit var presenter: FilterContract.FilterPresenter
    private lateinit var movieAdapter: FilterMovieAdapter

    private var categories = listOf<Category>()
    private var countries = listOf<Country>()
    private var years = listOf<String>()
    private var languages = listOf<String>()

    private var isLoadingMore = false

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFilterBinding {
        return FragmentFilterBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        super.initView()
        setupStatusBarPadding()
        setupPresenter()
        setupRecyclerView()
        setupClickListeners()
        loadInitialData()
        showInitialFilterState()
    }

    private fun setupStatusBarPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun setupPresenter() {
        val app = requireActivity().application as MyApp
        val repository = app.movieRepository
        presenter = FilterPresenter(repository)
        presenter.attachView(this)
    }

    private fun setupRecyclerView() {
        movieAdapter = FilterMovieAdapter { item ->
            onMovieClick(item)
        }

        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = movieAdapter
            isNestedScrollingEnabled = false
        }

        setupInfiniteScroll()
    }

    private fun setupInfiniteScroll() {
        binding.root.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY && !isLoadingMore) {
                val view = binding.root.getChildAt(binding.root.childCount - 1)
                val diff = (view.bottom - (binding.root.height + binding.root.scrollY))

                if (diff <= 100 && presenter.hasMoreData) {
                    isLoadingMore = true
                    binding.layoutLoadingMore.visibility = android.view.View.VISIBLE
                    presenter.loadMoreResults()
                }
            }
        }
    }

    private fun setupSpinner(spinner: Spinner, items: List<String>) {
        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun setupClickListeners() {
        binding.btnFilter.setOnClickListener {
            applyCurrentFilter()
        }

        binding.btnClearFilter.setOnClickListener {
            presenter.clearFilters()
        }
    }

    private fun loadInitialData() {
        presenter.loadCategories()
        presenter.loadCountries()
        presenter.loadYears()
        presenter.loadLanguages()
    }

    private fun showInitialFilterState() {
        binding.rvMovies.visibility = android.view.View.GONE
        binding.layoutLoading.visibility = android.view.View.GONE
        binding.tvEmptyResult.visibility = android.view.View.VISIBLE
        binding.tvEmptyResult.text = getString(R.string.filter_hint)
        binding.tvResultCount.text = getString(R.string.ready_to_search)
    }

    private fun applyCurrentFilter() {
        val typeList = getSelectedTypeList()
        val sortLang = getSelectedLanguage()
        val country = getSelectedCountry()
        val year = getSelectedYear()

        isLoadingMore = false
        presenter.applyFilter(typeList, sortLang, country, year, 1)
    }

    private fun getSelectedTypeList(): String {
        val position = binding.spinnerTypeList.selectedItemPosition
        return if (position > 0 && categories.isNotEmpty()) {
            categories[position - 1].slug
        } else ""
    }

    private fun getSelectedLanguage(): String? {
        val position = binding.spinnerSortLang.selectedItemPosition
        return if (position > 0 && languages.isNotEmpty()) {
            languages[position - 1]
        } else null
    }

    private fun getSelectedCountry(): String? {
        val position = binding.spinnerCountry.selectedItemPosition
        return if (position > 0 && countries.isNotEmpty()) {
            countries[position - 1].slug
        } else null
    }

    private fun getSelectedYear(): String? {
        val position = binding.spinnerYear.selectedItemPosition
        return if (position > 0 && years.isNotEmpty()) {
            years[position - 1]
        } else null
    }

    override fun showFilterResults(movies: List<Item>, currentPage: Int, totalPages: Int) {
        movieAdapter.submitList(movies)
        binding.layoutLoadingMore.visibility = android.view.View.GONE

        isLoadingMore = false

        if (movies.isEmpty()) {
            binding.rvMovies.visibility = android.view.View.GONE
            binding.tvEmptyResult.visibility = android.view.View.VISIBLE
            binding.tvEmptyResult.text = getString(R.string.no_result)
        } else {
            binding.rvMovies.visibility = android.view.View.VISIBLE
            binding.tvEmptyResult.visibility = android.view.View.GONE
        }
    }

    override fun showCategories(categories: List<Category>) {
        this.categories = categories
        val categoryNames = listOf(getString(R.string.all)) + categories.map { it.name }
        setupSpinner(binding.spinnerTypeList, categoryNames)
    }

    override fun showCountries(countries: List<Country>) {
        this.countries = countries
        val countryNames = listOf(getString(R.string.all)) + countries.map { it.name }
        setupSpinner(binding.spinnerCountry, countryNames)
    }

    override fun showYears(years: List<String>) {
        this.years = years
        val yearList = listOf(getString(R.string.all)) + years
        setupSpinner(binding.spinnerYear, yearList)
    }

    override fun showLanguages(languages: List<String>) {
        this.languages = languages
        val languageNames =
            listOf(getString(R.string.all)) + languages.map { LanguageMapper.getDisplayName(it) }
        setupSpinner(binding.spinnerSortLang, languageNames)
    }

    override fun updateResultCount(count: Int) {
        binding.tvResultCount.text = getString(R.string.movie_count, count)
    }

    override fun clearFilters() {
        isLoadingMore = false
        binding.spinnerTypeList.setSelection(0)
        binding.spinnerSortLang.setSelection(0)
        binding.spinnerCountry.setSelection(0)
        binding.spinnerYear.setSelection(0)
        movieAdapter.submitList(emptyList())
        binding.rvMovies.visibility = android.view.View.GONE
        binding.tvEmptyResult.visibility = android.view.View.VISIBLE
        binding.tvEmptyResult.text = getString(R.string.filter_empty)
    }

    override fun showEmptyResult() {
        binding.rvMovies.visibility = android.view.View.GONE
        binding.tvEmptyResult.visibility = android.view.View.VISIBLE
        binding.tvResultCount.text = getString(R.string.zero_movie)
    }

    override fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.layoutLoading.visibility = android.view.View.VISIBLE
            binding.rvMovies.visibility = android.view.View.GONE
            binding.tvEmptyResult.visibility = android.view.View.GONE
        } else {
            binding.layoutLoading.visibility = android.view.View.GONE
        }
    }

    override fun showError(message: String) {
        isLoadingMore = false
        binding.layoutLoadingMore.visibility = android.view.View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun onMovieClick(item: Item) {
        AppNavigator.navigateTo(
            NavDestination.MovieDetailScreen(item.slug),
            addToBackStack = true
        )
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
