package com.sun.moviedb.screen.chat

import com.sun.moviedb.data.model.MessageModel
import com.sun.moviedb.utils.base.BasePresenter
import com.sun.moviedb.utils.base.BaseView

interface ChatContract {
    interface View : BaseView {
        fun addMessages(message: MessageModel)
    }

    interface Presenter : BasePresenter<View> {
        fun receiveMessages(roomId: String)
        fun sendMessage(roomId: String, message: MessageModel)
    }
}

