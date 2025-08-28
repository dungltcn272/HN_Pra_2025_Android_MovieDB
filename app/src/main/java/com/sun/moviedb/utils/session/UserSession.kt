package com.sun.moviedb.utils.session

import com.google.firebase.auth.FirebaseUser

object UserSession {
    var userId: String? = null
    var userName: String? = null
    var linkAvatar: String? = null

    fun updateSession(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null)
            return
        userId = firebaseUser.uid
        userName = firebaseUser.displayName ?: ""
        linkAvatar = firebaseUser.photoUrl?.toString() ?: ""
    }
}

