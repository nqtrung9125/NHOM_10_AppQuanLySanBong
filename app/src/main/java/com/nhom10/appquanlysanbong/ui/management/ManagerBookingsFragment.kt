package com.nhom10.appquanlysanbong.ui.management

import com.nhom10.appquanlysanbong.data.booking.BookingRepository
import com.nhom10.appquanlysanbong.data.booking.ReservationState
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent
import com.nhom10.appquanlysanbong.ui.common.WorkspaceFragment
import com.nhom10.appquanlysanbong.ui.common.vnd

class ManagerBookingsFragment : WorkspaceFragment() {
    override val title = "Lịch đặt và hoàn cọc"
    override val role = Role.MANAGER

    override fun WorkspaceContent.content() {
        val repository = BookingRepository(requireContext())
        val bookings = repository.operations().sortedByDescending { it.state == ReservationState.REFUND_REQUESTED }
        if (bookings.isEmpty()) paragraph("Chưa có booking để theo dõi.")
        bookings.forEach { booking ->
            card(booking.courtName) {
                badge(booking.state.label)
                paragraph("${booking.contactName} · ${booking.dateLabel} · ${booking.timeLabel}\n${booking.id}")
                if (booking.state == ReservationState.REFUND_REQUESTED) {
                    button("Duyệt hoàn ${booking.deposit.vnd()}", primary = true) {
                        confirm("Duyệt hoàn cọc demo", "Khung giờ sẽ được mở lại khi duyệt.") {
                            perform("Đã duyệt hoàn cọc.") { repository.refund(booking.id) }
                        }
                    }
                }
            }
        }
    }
}
