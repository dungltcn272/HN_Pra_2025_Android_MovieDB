package com.sun.moviedb.data.repository

import kotlinx.coroutines.flow.Flow

interface ControllerRepository {

    fun listenForRoomCommandString(roomId: String): Flow<String?>
    suspend fun sendRoomCommandString(
        roomId: String,
        commandString: String
    ): Result<Unit>
}
