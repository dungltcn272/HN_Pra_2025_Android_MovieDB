package com.sun.moviedb.screen.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.sun.moviedb.MyApp
import com.sun.moviedb.R
import com.sun.moviedb.utils.base.BaseFragment
import com.sun.moviedb.databinding.FragmentHomeBinding
import com.sun.moviedb.data.model.Item
import com.sun.moviedb.screen.home.adapter.NewestMoviePagerAdapter
import com.sun.moviedb.screen.home.adapter.SeriesMovieAdapter
import com.sun.moviedb.screen.home.adapter.SeriesTabAdapter
import com.sun.moviedb.utils.navigation.AppNavigator
import com.sun.moviedb.utils.navigation.NavDestination
import com.sun.moviedb.screen.search.SearchDialogFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>(), HomeContract.HomeView {
    private lateinit var presenter: HomeContract.HomePresenter
    private lateinit var newestMovieAdapter: NewestMoviePagerAdapter
    private lateinit var seriesTabAdapter: SeriesTabAdapter
    private lateinit var seriesMovieAdapter: SeriesMovieAdapter

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        setupNewestMovieAdapter()
        setupSeriesTabAdapter()
        setupSeriesMovieAdapter()
        setupSearchClick()
        setupUserInteraction()
    }

    private fun onMessageClick() {
        binding.btnMessage.setOnClickListener {
            AppNavigator.navigateTo(NavDestination.ChatScreen, addToBackStack = true)
            Toast.makeText(requireContext(), "Message clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUserInteraction() {
        val userAvatarUrl = FirebaseAuth.getInstance().currentUser?.photoUrl
        if (userAvatarUrl != null) {
            binding.avatarUserImage.apply {
                Glide.with(this)
                    .load(userAvatarUrl)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .into(this)
            }
        }
        binding.avatarUserImage.setOnClickListener {
            AppNavigator.navigateTo(NavDestination.ProfileScreen, addToBackStack = true)
        }
    }

    private fun setupNewestMovieAdapter() {
        newestMovieAdapter = NewestMoviePagerAdapter(
            onWatchNowClick = { item -> onMovieClick(item) }
        )
        binding.newestMovieViewPager.adapter = newestMovieAdapter
        binding.newestMovieIndicator.attachTo(binding.newestMovieViewPager)
    }

    private fun setupSeriesTabAdapter() {
        seriesTabAdapter = SeriesTabAdapter { selectedSeries ->
            presenter.selectSeries(selectedSeries)
        }
        binding.seriesTabRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = seriesTabAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSeriesMovieAdapter() {
        seriesMovieAdapter = SeriesMovieAdapter(
            onMovieClick = { item -> onMovieClick(item) }
        )
        binding.seriesMovieRecycler.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = seriesMovieAdapter
        }
    }

    private fun setupSearchClick() {
        binding.btnSearch.setOnClickListener {
            val dialog = SearchDialogFragment()
            dialog.show(parentFragmentManager, "SearchDialogFragment")
        }
    }

    override fun initData() {
        val app = requireActivity().application as MyApp
        presenter =
            HomePresenter(app.movieRepository)
        presenter.attachView(this)
        val series = presenter.seriesList.first()
        val page = 1
        seriesTabAdapter.submitList(presenter.seriesList)
        seriesTabAdapter.setSelectedTab(series)
        presenter.loadNewestMovies()
        presenter.loadSeriesMovies(series, page)
        onMessageClick()
    }

    override fun showNewestMovies(items: List<Item>) {
        newestMovieAdapter.submitList(items)
    }

    override fun showSeriesMovies(items: List<Item>, currentPage: Int, totalPage: Int) {
        seriesMovieAdapter.submitList(items)
        if (items.isNotEmpty()) {
            binding.seriesMoviePaginationView.visibility = ViewGroup.VISIBLE
            binding.seriesMoviePaginationView.setup(totalPage, currentPage) { page ->
                presenter.selectPage(page)
            }
        } else {
            binding.seriesMoviePaginationView.visibility = ViewGroup.GONE
        }
    }

    override fun showNewestMoviesLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingNewestMovieView.visibility = ViewGroup.VISIBLE
        } else {
            binding.loadingNewestMovieView.visibility = ViewGroup.GONE
        }
    }

    override fun showSeriesMoviesLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.seriesMovieLoadingView.visibility = ViewGroup.VISIBLE
        } else {
            binding.seriesMovieLoadingView.visibility = ViewGroup.GONE
        }
    }

    private fun onMovieClick(item: Item) {
        AppNavigator.navigateTo(NavDestination.MovieDetailScreen(item.slug), addToBackStack = true)
    }

    override fun showLoading(isLoading: Boolean) {}
    override fun showError(message: String) {}

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }
}
