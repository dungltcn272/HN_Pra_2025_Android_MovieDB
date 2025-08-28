package com.sun.moviedb.screen.room

import com.sun.moviedb.utils.base.BasePresenter
import com.sun.moviedb.utils.base.BaseView

interface RoomContract {
    interface View : BaseView

    interface Presenter : BasePresenter<View>

}