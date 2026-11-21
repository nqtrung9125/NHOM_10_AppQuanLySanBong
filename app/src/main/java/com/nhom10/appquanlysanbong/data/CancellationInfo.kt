package com.nhom10.appquanlysanbong.data

data class CancellationInfo(
    val bookingCode: String,
    val courtName: String,
    val dateTime: String,
    val depositAmount: String,
    val refundAmount: String,
    val refundMethod: String,
    val refundPolicy: String
)