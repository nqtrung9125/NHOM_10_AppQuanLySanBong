package com.nhom10.appquanlysanbong.data

data class AppNotification(
    val title: String,
    val message: String,
    val time: String,
    val icon: String,
    val isUnread: Boolean
)