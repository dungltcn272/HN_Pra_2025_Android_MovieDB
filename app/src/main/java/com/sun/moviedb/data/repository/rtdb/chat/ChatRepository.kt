package com.sun.moviedb.data.repository.rtdb

import com.sun.moviedb.data.model.MessageModel
import com.sun.moviedb.data.repository.source.remote.NetworkResult

interface ChatRepository {
    fun sendMessage(
        roomId: String,
        message: MessageModel,
        callback: (NetworkResult<MessageModel>) -> Unit
    )

    fun receiveMessages(roomId: String, onResult: (NetworkResult<MessageModel>) -> Unit)
    fun removeListener(roomId: String)
    fun deleteChatNode(roomId: String, callback: (NetworkResult<Unit>) -> Unit)
}
