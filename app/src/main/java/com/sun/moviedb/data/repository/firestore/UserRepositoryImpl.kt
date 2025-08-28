package com.sun.moviedb.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sun.moviedb.data.model.User
import com.sun.moviedb.data.repository.firestore.UserRepository
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl : UserRepository {

    private val usersCollection = FirebaseFirestore.getInstance().collection("users")
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "UserRepository"

    override suspend fun searchUsers(query: String): Result<List<User>> {
        val currentUserId = firebaseAuth.currentUser?.uid

        return try {
            if (query.isBlank()) {
                return Result.success(emptyList())
            }
            val fieldToSearch = "username"

            val querySnapshot = usersCollection
                .orderBy(fieldToSearch)
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(20)
                .get()
                .await()

            val users = querySnapshot.documents.mapNotNull { document ->
                try {
                    val user = document.toObject(User::class.java)
                    user?.id = document.id
                    if (user != null && user.id == currentUserId) {
                        null
                    } else {
                        user
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error converting document to User during search: ${document.id}", e)
                    null
                }
            }
            Result.success(users)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching users in Firestore", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        val currentUserId = firebaseAuth.currentUser?.uid

        return try {
            val querySnapshot = usersCollection
                .orderBy("username", Query.Direction.ASCENDING)
                .limit(50)
                .get()
                .await()

            val users = querySnapshot.documents.mapNotNull { document ->
                try {
                    val user = document.toObject(User::class.java)
                    user?.id = document.id
                    if (user != null && user.id == currentUserId) {
                        null
                    } else {
                        user
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error converting document to User during getAllUsers: ${document.id}", e)
                    null
                }
            }
            Result.success(users)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching all users from Firestore", e)
            Result.failure(e)
        }
    }
}

