package com.sun.moviedb.data.repository.auth

import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.sun.moviedb.R
import com.sun.moviedb.data.model.User
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(private val context: Context) : AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient
    private val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val TAG = "AuthRepository"

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    override fun getGoogleSignInClientIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    @OptIn(UnstableApi::class)
    override suspend fun handleGoogleSignInResult(account: GoogleSignInAccount?): Result<FirebaseUser> {
        return try {
            if (account?.idToken == null) {
                Result.failure(IllegalArgumentException("Google Sign-In account or ID token is null."))
            } else {
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    saveUserToFirestore(firebaseUser)
                    Result.success(firebaseUser)
                } else {
                    Result.failure(Exception("Firebase authentication failed: User is null."))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling Google Sign-In result or interacting with Firestore", e)
            Result.failure(e)
        }
    }

    @OptIn(UnstableApi::class)
    private suspend fun saveUserToFirestore(firebaseUser: FirebaseUser) {
        val userDocumentRef = firestoreDb.collection("users").document(firebaseUser.uid)

        try {
            val documentSnapshot = userDocumentRef.get().await()

            if (!documentSnapshot.exists()) {
                val newUser = User(
                    id = firebaseUser.uid,
                    username = firebaseUser.displayName,
                    email = firebaseUser.email,
                    profileImageUrl = firebaseUser.photoUrl?.toString()
                )
                userDocumentRef.set(newUser).await()
                Log.d(
                    TAG,
                    "New user added to Firestore with UID: ${firebaseUser.uid} and document field id: ${newUser.id}"
                )
            } else {
                Log.d(
                    TAG,
                    "User with UID: ${firebaseUser.uid} already exists in Firestore. No action taken to add."
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking/adding user in Firestore for UID: ${firebaseUser.uid}", e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            googleSignInClient.signOut().await()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}
