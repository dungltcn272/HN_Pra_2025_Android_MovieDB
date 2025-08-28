package com.sun.moviedb.screen.login

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.sun.moviedb.utils.base.BasePresenter
import com.sun.moviedb.utils.base.BaseView

interface LoginContract {
    interface View : BaseView {
        fun showLoginSuccess(firebaseUser: FirebaseUser)
        fun showLoginError(message: String)
        fun navigateToMain()
        fun launchGoogleSignInActivity(signInIntent: Intent)
        fun showGoogleSignInFailed(message: String)
    }

    interface Presenter : BasePresenter<View> {
        fun onLoginButtonClicked()
        fun handleGoogleSignInResult(account: GoogleSignInAccount?, exception: Exception?)
    }

    interface Interactor {
        interface OnAuthFinishedListener {
            fun onAuthSuccess(firebaseUser: FirebaseUser)
            fun onAuthFailure(message: String)
        }

        suspend fun performGoogleLogin(
            account: GoogleSignInAccount?,
            listener: OnAuthFinishedListener
        )
    }
}