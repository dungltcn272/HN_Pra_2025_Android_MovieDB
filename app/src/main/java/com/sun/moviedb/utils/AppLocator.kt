package com.sun.moviedb.utils

import com.sun.moviedb.data.repository.rtdb.member.MemberRepository
import com.sun.moviedb.data.repository.rtdb.member.MemberRepositoryImpl
import com.sun.moviedb.data.repository.rtdb.room.RoomRepository
import com.sun.moviedb.data.repository.rtdb.room.RoomRepositoryImpl

object AppLocator {
    val roomRepository: RoomRepository by lazy {
        RoomRepositoryImpl.getInstance()
    }

    val memberRepository: MemberRepository by lazy {
        MemberRepositoryImpl.getInstance()
    }
}

