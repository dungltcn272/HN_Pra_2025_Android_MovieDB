package com.sun.moviedb.screen.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.moviedb.MyApp
import com.sun.moviedb.data.model.Movie
import com.sun.moviedb.databinding.FragmentFavoriteMovieBinding
import com.sun.moviedb.screen.favorite.adapter.FavoriteMovieAdapter
import com.sun.moviedb.utils.base.BaseFragment
import com.sun.moviedb.utils.navigation.AppNavigator
import com.sun.moviedb.utils.navigation.NavDestination

class FavoriteMovieFragment : BaseFragment<FragmentFavoriteMovieBinding>(),
    FavoriteMovieContract.FavoriteMovieView {

    private lateinit var favoriteMovieAdapter: FavoriteMovieAdapter
    private lateinit var presenter: FavoriteMovieContract.FavoriteMoviePresenter

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteMovieBinding {
        return FragmentFavoriteMovieBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        super.initView()
        setupStatusBarPadding()
        setupRecyclerView()
    }

    override fun initData() {
        super.initData()
        val app = requireActivity().application as MyApp
        presenter = FavoriteMoviePresenter(app.movieRepository)
        presenter.attachView(this)
        presenter.getFavoriteMovies()
    }


    private fun setupRecyclerView() {
        favoriteMovieAdapter = FavoriteMovieAdapter { movie ->
            onMovieClick(movie)
        }
        binding.rvFavoriteMovies.apply {
            setHasFixedSize(true)
            adapter = favoriteMovieAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }


    private fun setupStatusBarPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    override fun showFavoriteMovies(movies: List<Movie>) {
        if (movies.isEmpty()) {
            showEmptyResult(true)
        } else {
            showEmptyResult(false)
            favoriteMovieAdapter.submitList(movies)
        }
    }

    override fun showLoading(isLoading: Boolean) {
        binding.layoutLoading.visibility = if (isLoading) ViewGroup.VISIBLE else ViewGroup.GONE
    }

    override fun showError(message: String) {}

    private fun onMovieClick(movie: Movie) {
        AppNavigator.navigateTo(
            NavDestination.MovieDetailScreen(slug = movie.slug),
            addToBackStack = true
        )
    }

    private fun showEmptyResult(isShow: Boolean) {
        binding.tvEmptyResult.visibility = if (isShow) ViewGroup.VISIBLE else ViewGroup.GONE
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

}

