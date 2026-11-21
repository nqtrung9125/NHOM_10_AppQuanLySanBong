package com.nhom10.appquanlysanbong.data.booking

import com.nhom10.appquanlysanbong.data.Court
import com.nhom10.appquanlysanbong.data.Sport
import com.nhom10.appquanlysanbong.data.courts.displayName
import com.nhom10.appquanlysanbong.data.session.DemoAccounts
import com.nhom10.appquanlysanbong.data.session.Role
import java.util.Calendar

internal fun staffBookingExamplePrefix(day: String) = "DS${day.replace("-", "")}-MAU"

internal val Reservation.isStaffExample: Boolean
    get() = id.substringAfter('-').startsWith("MAU")

/** Dùng giờ hiện tại để tạo các tình huống quay demo trên lịch chung. */
internal fun staffBookingExamples(courts: List<Court>, existing: List<Reservation>, now: Long): List<Reservation> {
    val today = ScheduleDays.date(now = now)
    val tomorrow = ScheduleDays.date(1, now)
    val hour = ScheduleDays.calendar(today).apply { timeInMillis = now }.get(Calendar.HOUR_OF_DAY)
    val available = courts.filter(Court::available)
    val customers = DemoAccounts.accounts.filter { it.role == Role.CUSTOMER }
    val created = mutableListOf<Reservation>()

    fun add(state: ReservationState, sport: Sport, day: String, start: Int, end: Int) {
        val candidates = available.sortedBy { it.sport != sport }
        val court = candidates.firstOrNull { candidate ->
            (existing.asSequence() + created.asSequence()).none {
                BookingRules.overlaps(it, candidate.id, day, start, end, now)
            }
        } ?: return
        val owner = customers[created.size % customers.size]
        created += Reservation(
            id = staffBookingExamplePrefix(today) + (created.size + 1).toString().padStart(2, '0'),
            ownerId = owner.id,
            courtId = court.id,
            courtName = court.displayName,
            day = day,
            start = start,
            end = end,
            total = BookingRules.quote(court, day, start, end),
            state = state,
            expiresAt = if (state == ReservationState.HELD) {
                minOf(now + BookingRules.HOLD_MINUTES * 60_000, ScheduleDays.startMillis(day, start))
            } else 0,
            contactName = owner.name,
            depositMethod = if (state == ReservationState.HELD) null else DepositMethod.COUNTER
        )
    }

    // Ngoài giờ mở cửa, phiếu đang thuê minh họa tình huống đã quá giờ trả sân.
    val rentalDay = if (hour < 5) ScheduleDays.date(-1, now) else today
    val rentalStart = if (hour < 5) 21 else hour.coerceAtMost(21)
    add(ReservationState.CHECKED_IN, Sport.BADMINTON, rentalDay, rentalStart, rentalStart + 1)

    val upcomingDay = if (hour >= 21) tomorrow else today
    val upcomingStart = if (hour >= 21) 9 else (hour + 1).coerceAtLeast(5)
    add(ReservationState.PAID, Sport.FOOTBALL, upcomingDay, upcomingStart, upcomingStart + 1)
    add(ReservationState.HELD, Sport.PICKLEBALL, upcomingDay, upcomingStart, upcomingStart + 1)

    val completedDay = if (hour >= 6) today else ScheduleDays.date(-1, now)
    add(ReservationState.COMPLETED, Sport.PICKLEBALL, completedDay, 5, 6)
    val refundDay = ScheduleDays.date(2, now)
    add(ReservationState.REFUND_REQUESTED, Sport.BADMINTON, refundDay, 18, 19)
    add(ReservationState.REFUNDED, Sport.FOOTBALL, refundDay, 19, 20)
    return created
}
