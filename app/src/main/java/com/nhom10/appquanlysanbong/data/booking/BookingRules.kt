package com.nhom10.appquanlysanbong.data.booking

import com.nhom10.appquanlysanbong.data.Court
import com.nhom10.appquanlysanbong.data.courts.rate
import java.util.Calendar

object BookingRules {
    const val HOLD_MINUTES = 15
    const val CHECK_IN_EARLY_MINUTES = 15
    const val DEPOSIT_PERCENT = 30
    const val REFUND_HOURS = 12L

    fun deposit(total: Int) = (total.toLong() * DEPOSIT_PERCENT / 100).toInt()

    fun quote(court: Court, day: String, start: Int, end: Int): Int {
        require(start in 5..21 && end in (start + 1)..22) { "Chọn giờ trong khoảng 05:00–22:00." }
        val weekday = ScheduleDays.calendar(day).get(Calendar.DAY_OF_WEEK)
        val weekend = weekday == Calendar.SATURDAY || weekday == Calendar.SUNDAY
        return (start until end).sumOf { hour ->
            court.rate(if (hour < 16) 0 else if (weekend) 2 else 1)
        }
    }

    // Hai suất có thể dùng chung mốc kết thúc/bắt đầu. Giữ sân hết hạn không chặn lịch.
    fun overlaps(reservation: Reservation, courtId: String, day: String, start: Int, end: Int, now: Long) =
        reservation.courtId == courtId && reservation.day == day &&
            reservation.start < end && start < reservation.end &&
            effectiveState(reservation, now).occupiesSlot

    fun isWithinCheckInWindow(reservation: Reservation, now: Long): Boolean {
        if (reservation.state != ReservationState.PAID || reservation.day != ScheduleDays.date(now = now)) return false
        val start = ScheduleDays.startMillis(reservation.day, reservation.start)
        val end = ScheduleDays.startMillis(reservation.day, reservation.end)
        return now >= start - CHECK_IN_EARLY_MINUTES * 60_000L && now < end
    }

    fun effectiveState(reservation: Reservation, now: Long): ReservationState =
        if (reservation.state == ReservationState.HELD && reservation.expiresAt <= now) {
            ReservationState.EXPIRED
        } else reservation.state
}
