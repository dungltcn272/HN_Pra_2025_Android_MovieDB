package com.sun.moviedb.data.repository.rtdb.room

import com.sun.moviedb.data.model.Room
import com.sun.moviedb.data.repository.source.remote.NetworkResult

interface RoomRepository {
    fun addRoom(room: Room, onResult: (NetworkResult<Unit>) -> Unit)
    fun getRoom(roomId: String, onResult: (NetworkResult<Room>) -> Unit)
    fun deleteRoom(roomId: String, onResult: (NetworkResult<Unit>) -> Unit)
    fun getCommand(roomId: String, onResult: (NetworkResult<String>) -> Unit)
    fun removeListener(roomId: String)
}

