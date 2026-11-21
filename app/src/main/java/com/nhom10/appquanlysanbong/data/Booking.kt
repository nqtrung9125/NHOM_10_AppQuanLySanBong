package com.nhom10.appquanlysanbong.data

import androidx.annotation.ColorRes
import com.nhom10.appquanlysanbong.R

enum class BookingStatus(
    val label: String,
    @ColorRes val chipBg: Int,
    @ColorRes val chipText: Int,
    @ColorRes val noteColor: Int,
    val noteMedium: Boolean
) {
    DEPOSIT_PAID("Đã thanh toán cọc", R.color.brand_tint, R.color.brand, R.color.text_muted, false),
    PENDING_DEPOSIT("Chờ thanh toán cọc", R.color.warning_bg, R.color.warning_text, R.color.warning_text, true),
    COMPLETED("Hoàn thành", R.color.brand_tint, R.color.brand_chip, R.color.text_muted, false),
    REFUNDED("Hoàn cọc", R.color.success_bg, R.color.success, R.color.success, true),
    FORFEITED("Mất cọc", R.color.danger, android.R.color.white, R.color.danger, true),
    CANCELLED("Đã hủy", R.color.danger_bg, R.color.danger, R.color.text_muted, false),
    PASSING("Đang pass", R.color.brand_tint, R.color.brand, R.color.text_muted, false),
    TRANSFERRED("Đã chuyển nhượng", R.color.success_bg, R.color.success, R.color.success, false)
}

data class Booking(
    val id: String,
    val court: String,
    val date: String,
    val time: String,
    val total: String,
    val note: String,
    val status: BookingStatus,
    val weatherAlert: String? = null,
    val canCancel: Boolean = false,
    val payAmount: String? = null,
    val canPass: Boolean = false
)
