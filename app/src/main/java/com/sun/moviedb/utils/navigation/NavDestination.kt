package com.sun.moviedb.utils.navigation

sealed class NavDestination {
    data object HomeScreen : NavDestination()
    data object NotificationScreen : NavDestination()
    data object FilterScreen : NavDestination()
    data object FavoriteMovieScreen : NavDestination()
    data object InviteFriendScreen : NavDestination()
    data object ChatScreen : NavDestination()
    data object ProfileScreen : NavDestination()
    data class MovieDetailScreen(val slug: String) : NavDestination()
}
