package com.sun.moviedb.screen.searchUser

import com.sun.moviedb.data.model.User
import com.sun.moviedb.utils.base.BasePresenter
import com.sun.moviedb.utils.base.BaseView

interface SearchUserContract {
    interface View : BaseView {
        fun showLoading()
        fun hideLoading()
        fun displaySearchableUsers(users: List<User>)
        fun displayChosenUsers(users: List<User>)
        fun removeUserFromSearchableList(user: User)
        fun addUserToSearchableList(user: User)
        fun removeUserFromChosenList(user: User)
        fun addUserToChosenList(user: User)
        fun showSearchableUsersEmpty(message: String)
        fun hideSearchableUsersEmpty()
        fun showChosenUsersEmpty(message: String)
        fun hideChosenUsersEmpty()
        fun displayError(message: String)
        fun updateInviteButton(count: Int, isEnabled: Boolean)
        fun showInviteSentSuccess(count: Int)
        fun showInviteSentError(message: String)
        fun showSendingInvitesLoading(isLoading: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun loadInitialUsers()
        fun searchUsers(query: String)
        fun selectUser(user: User)
        fun deselectUser(user: User)
        fun onInviteClicked()
    }
}

