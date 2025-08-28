package com.sun.moviedb.screen

import com.sun.moviedb.R
import com.sun.moviedb.utils.base.BaseActivity
import com.sun.moviedb.databinding.ActivityMainBinding
import com.sun.moviedb.utils.navigation.AppNavigator
import com.sun.moviedb.utils.navigation.NavDestination

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        /* *
        * Initalize an instance of AppNavigator to handle navigation
        * */
        AppNavigator.init(
            supportFragmentManager,
            binding.fragmentContainer.id,
            binding.bottomNavigation
        )

        /* *
        * bottom navigation bar attach to
        * */
        AppNavigator.attachNavVisibilityListener()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    AppNavigator.navigateTo(NavDestination.HomeScreen)
                    true
                }
                R.id.nav_filter -> {
                    AppNavigator.navigateTo(NavDestination.FilterScreen)
                    true
                }
                R.id.nav_notification -> {
                    AppNavigator.navigateTo(NavDestination.NotificationScreen)
                    true
                }
                R.id.nav_favorite -> {
                    AppNavigator.navigateTo(NavDestination.FavoriteMovieScreen)
                    true
                }
                else -> false
            }
        }
        // Set default selected item (start destination)
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }

    override fun initData() {
        super.initData()


    }
}
