package com.nhom10.appquanlysanbong.data

import java.text.NumberFormat
import java.util.Locale

enum class Sport(val label: String) {
    FOOTBALL("Bóng đá"),
    BADMINTON("Cầu lông"),
    PICKLEBALL("Pickleball"),
    VOLLEYBALL("Bóng chuyền")
}

data class PriceRow(val label: String, val hourlyRate: Int) {
    val price: String get() = "${formatVndAmount(hourlyRate)}đ/giờ"
}

internal fun formatVndAmount(amount: Int): String = formatVndAmount(amount.toLong())

internal fun formatVndAmount(amount: Long): String =
    NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN")).format(amount)

data class Court(
    val id: String,
    val name: String,
    val sport: Sport,
    val surface: String,
    val hasLight: Boolean,
    val priceFrom: String,
    val available: Boolean,
    val maxPlayers: Int,
    val openHours: String,
    val deposit: String,
    val depositDeadline: String,
    val prices: List<PriceRow>
) {
    /** Ví dụ: "Cầu lông · Thảm PVC · Có đèn" */
    val description: String
        get() = buildString {
            append(sport.label).append(" · ").append(surface)
            if (hasLight) append(" · Có đèn")
        }
}
