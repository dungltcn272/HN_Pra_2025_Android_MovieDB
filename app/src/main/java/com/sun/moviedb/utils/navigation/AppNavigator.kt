package com.sun.moviedb.utils.navigation

import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sun.moviedb.screen.detail.MovieDetailFragment
import com.sun.moviedb.screen.filter.FilterFragment
import com.sun.moviedb.screen.home.HomeFragment
import com.sun.moviedb.screen.notification.NotificationFragment
import com.sun.moviedb.screen.settings.SettingsFragment

object AppNavigator {
    private var fragmentManager: FragmentManager? = null
    private var containerId: Int = 0
    private var bottomNavView: BottomNavigationView? = null

    fun init(
        fragmentManager: FragmentManager,
        containerId: Int,
        bottomNavView: BottomNavigationView
    ) {
        AppNavigator.fragmentManager = fragmentManager
        AppNavigator.containerId = containerId
        AppNavigator.bottomNavView = bottomNavView
    }

    fun navigateTo(
        destination: NavDestination,
        addToBackStack: Boolean = false
    ) {
        val tag = destination::class.simpleName
        val fm = fragmentManager ?: return

        var fragment = fm.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = when (destination) {
                is NavDestination.HomeScreen -> HomeFragment()
                is NavDestination.NotificationScreen -> NotificationFragment()
                is NavDestination.MovieDetailScreen -> MovieDetailFragment.newInstance(destination.slug)
                is NavDestination.SettingsScreen -> SettingsFragment()
                is NavDestination.FilterScreen -> FilterFragment()
                else -> throw IllegalArgumentException("Unknown destination: $destination")
            }
        }

        fm.beginTransaction().apply {
            if (addToBackStack) {
                fm.fragments.forEach { hide(it) }
                if (!fragment.isAdded) {
                    add(containerId, fragment, tag)
                } else {
                    show(fragment)
                }
                addToBackStack(tag)
            } else {
                replace(containerId, fragment, tag)
            }
            commit()
        }
    }

    fun attachNavVisibilityListener() {
        fragmentManager?.addOnBackStackChangedListener {
            val currentFragment = fragmentManager?.findFragmentById(containerId)
            val isVisibleBottomBar = currentFragment is HomeFragment ||
                    currentFragment is NotificationFragment ||
                    currentFragment is SettingsFragment ||
                    currentFragment is FilterFragment
            bottomNavView?.visibility = if (isVisibleBottomBar) View.VISIBLE else View.GONE
        }
    }
}
