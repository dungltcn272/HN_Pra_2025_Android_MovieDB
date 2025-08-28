package com.sun.moviedb.utils.session

object RoomSession {
    var roomId: String? = null
    var roomName: String? = null
    var movieLink:String?=null

    fun updateRoomId(newRoomId: String) {
        roomId = newRoomId
    }
    fun clearAll() {
        roomId = null
        roomName = null
        movieLink = null
    }

    fun updateRoomName(newRoomName: String) {
        roomName = newRoomName
    }
    fun updateMovieLink(newMovieLink: String) {
        movieLink = newMovieLink
    }
}

