package com.nhom10.appquanlysanbong.data.booking

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class ReservationState(val label: String) {
    HELD("Đang giữ sân"),
    PAID("Đã cọc"),
    CHECKED_IN("Đã nhận sân"),
    COMPLETED("Đã trả sân"),
    PASSING("Đang pass sân"),
    TRANSFERRED("Đã chuyển nhượng"),
    REFUND_REQUESTED("Chờ xử lý hoàn cọc"),
    REFUNDED("Đã hoàn cọc"),
    CANCELLED("Đã hủy"),
    EXPIRED("Hết hạn giữ sân");

    val occupiesSlot: Boolean
        get() = this in setOf(HELD, PAID, CHECKED_IN, COMPLETED, PASSING, REFUND_REQUESTED)
}

enum class DepositMethod(val label: String) {
    TRANSFER("Chuyển khoản"),
    WALLET("Ví điện tử"),
    CARD("Thẻ"),
    COUNTER("Thu tại quầy")
}

data class Reservation(
    val id: String, val ownerId: String, val courtId: String, val courtName: String,
    val day: String, val start: Int, val end: Int, val total: Int,
    val state: ReservationState, val expiresAt: Long = 0, val passPrice: Int = 0,
    val contactName: String = "",
    val depositMethod: DepositMethod? = null
) {
    val deposit get() = BookingRules.deposit(total)
    val timeLabel get() = String.format(Locale.ROOT, "%02d:00 – %02d:00", start, end)
    val dateLabel get() = day.split('-').asReversed().joinToString("/")
}

object ScheduleDays {
    private fun formatter() = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }

    fun date(offset: Int = 0, now: Long = System.currentTimeMillis()): String =
        Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.DAY_OF_YEAR, offset)
        }.let { formatter().format(it.time) }

    fun startMillis(day: String, hour: Int): Long = Calendar.getInstance().apply {
        time = requireNotNull(formatter().parse(day))
        set(Calendar.HOUR_OF_DAY, hour)
    }.timeInMillis

    fun calendar(day: String): Calendar =
        Calendar.getInstance().apply { time = requireNotNull(formatter().parse(day)) }

    fun deadline(time: Long): String =
        SimpleDateFormat("HH:mm dd/MM", Locale.ROOT).format(Date(time))
}
