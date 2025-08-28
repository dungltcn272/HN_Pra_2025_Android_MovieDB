package com.sun.moviedb.data.repository.source.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.sun.moviedb.data.repository.source.firebase.entity.MovieFirebaseEntity
import com.sun.moviedb.data.repository.source.firebase.entity.SearchKeywordFirebaseEntity

fun addFavoriteMovie(
    db: FirebaseFirestore,
    userId: String,
    movie: MovieFirebaseEntity,
    onComplete: (Boolean) -> Unit
) {
    db.collection("favourites").document(userId)
        .collection("movies").document(movie.id)
        .set(movie)
        .addOnSuccessListener { onComplete(true) }
        .addOnFailureListener { onComplete(false) }
}

fun removeFavoriteMovie(
    db: FirebaseFirestore,
    userId: String,
    movieId: String,
    onComplete: (Boolean) -> Unit
) {
    db.collection("favourites").document(userId)
        .collection("movies").document(movieId)
        .delete()
        .addOnSuccessListener { onComplete(true) }
        .addOnFailureListener { onComplete(false) }
}

fun getFavoriteMovies(
    db: FirebaseFirestore,
    userId: String,
    onResult: (List<MovieFirebaseEntity>) -> Unit
) {
    db.collection("favourites").document(userId)
        .collection("movies")
        .get()
        .addOnSuccessListener { result ->
            val movies = result.documents.mapNotNull {
                it.toObject(MovieFirebaseEntity::class.java)
            }
            onResult(movies)
        }
        .addOnFailureListener { onResult(emptyList()) }
}


fun addSearchKeyword(
    db: FirebaseFirestore,
    userId: String,
    keyword: String,
    onComplete: (Boolean) -> Unit
) {
    val keywordRef = db.collection("search_keywords").document(userId)
        .collection("keywords")
    val entity = SearchKeywordFirebaseEntity(
        keyword = keyword,
        timestamp = System.currentTimeMillis()
    )

    keywordRef.document(keyword)
        .set(entity)
        .addOnSuccessListener {
            keywordRef.orderBy(
                "timestamp",
                com.google.firebase.firestore.Query.Direction.DESCENDING
            )
                .get()
                .addOnSuccessListener { snapshot ->
                    val docs = snapshot.documents
                    if (docs.size > 10) {
                        docs.drop(10).forEach { it.reference.delete() }
                    }
                    onComplete(true)
                }
                .addOnFailureListener { onComplete(false) }
        }
        .addOnFailureListener { onComplete(false) }
}

fun getRecentSearchKeywords(
    db: FirebaseFirestore,
    userId: String,
    limit: Long = 10,
    onResult: (List<String>) -> Unit
) {
    db.collection("search_keywords").document(userId)
        .collection("keywords")
        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
        .limit(limit)
        .get()
        .addOnSuccessListener { result ->
            val keywords = result.documents.mapNotNull {
                it.toObject(SearchKeywordFirebaseEntity::class.java)?.keyword
            }
            onResult(keywords)
        }
        .addOnFailureListener { onResult(emptyList()) }
}
