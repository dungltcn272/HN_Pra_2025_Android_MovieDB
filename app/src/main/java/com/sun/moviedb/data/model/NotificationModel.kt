package com.sun.moviedb.data.model

sealed class NotificationModel {
    abstract val id: String
    abstract val type: String
    abstract val title: String
    abstract val body: String
    abstract val createAt: Long
    abstract var isRead: Boolean

    data class Invite(
        override val id: String = "",
        override val type: String = NotificationType.INVITE,
        override val title: String = "",
        override val body: String = "",
        override val createAt: Long = 0L,
        override var isRead: Boolean = false,
        val roomId: String = "",
        val roomName: String = "",
        val senderId: String = "",
        val senderName: String = "",
        val senderAvatar: String = "",
        val movieName: String = "",
        val movieLink: String = "",
    ) : NotificationModel()

    data class System(
        override val id: String = "",
        override val type: String = NotificationType.SYSTEM,
        override val title: String = "",
        override val body: String = "",
        override val createAt: Long = 0L,
        override var isRead: Boolean = false
    ) : NotificationModel()
}

object NotificationType {
    const val INVITE = "invite"
    const val SYSTEM = "system"
}

