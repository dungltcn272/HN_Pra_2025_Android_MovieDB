package com.sun.moviedb.data.repository.rtdb.notification

import com.sun.moviedb.data.model.NotificationModel

interface NotificationsFetchListener {
    fun onNotificationsReceived(notifications: List<NotificationModel>)
    fun onError(exception: Exception)
}
interface NotificationOperationListener {
    fun onSuccess()
    fun onError(exception: Exception)
}

interface NotificationRepository {
    fun listenForUserNotifications(listener: NotificationsFetchListener)
    fun removeNotificationsListener()
    fun markNotificationAsRead(notificationId: String, listener: NotificationOperationListener)
    fun addNotification(targetUserId: String, notification: NotificationModel, listener: NotificationOperationListener)
}
