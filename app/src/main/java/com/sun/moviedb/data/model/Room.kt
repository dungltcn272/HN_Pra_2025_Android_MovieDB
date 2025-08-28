package com.sun.moviedb.data.model

data class Room(
    var roomId: String = "",
    var roomName: String = "", // Name of Movie
    var roomCode: String = "", // Movie slug
    var createAt: Long = 0L,
    var createBy: String = "", // User ID of the creator
    var command: String = "",
)

