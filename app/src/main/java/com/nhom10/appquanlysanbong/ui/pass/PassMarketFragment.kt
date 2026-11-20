package com.nhom10.appquanlysanbong.ui.pass

import com.nhom10.appquanlysanbong.data.booking.BookingRepository
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.ui.booking.ReservationDetailFragment
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent
import com.nhom10.appquanlysanbong.ui.common.WorkspaceFragment
import com.nhom10.appquanlysanbong.ui.common.vnd
import com.nhom10.appquanlysanbong.ui.history.BookingHistoryFragment

class PassMarketFragment : WorkspaceFragment() {
    override val title = "Chợ pass sân"
    override val role = Role.CUSTOMER

    override fun WorkspaceContent.content() {
        val repository = BookingRepository(requireContext())
        val passes = repository.market()
        paragraph("Để đăng pass, mở một booking đã cọc trong lịch sử.")
        button("Lịch sử đặt sân") { navigator.navigateAsRoot(BookingHistoryFragment()) }
        if (passes.isEmpty()) emptyState("Chưa có suất đang pass.")
        passes.forEach { booking ->
            card(booking.courtName) {
                valueRow("Ngày chơi", booking.dateLabel)
                valueRow("Khung giờ", booking.timeLabel)
                badge("Giá pass ${booking.passPrice.vnd()}")
                valueRow("Còn trả tại sân", (booking.total - booking.deposit).vnd())
                if (booking.ownerId == session.current.id) {
                    button("Xem tin của tôi") { navigator.navigateTo(ReservationDetailFragment.forId(booking.id)) }
                } else {
                    button("Nhận sân pass", primary = true) {
                        confirm("Nhận sân pass", "Xác nhận thanh toán demo ${booking.passPrice.vnd()} và tiếp quản booking?") {
                            perform("Đã nhận sân pass.") {
                                val received = repository.takePass(booking.id)
                                navigator.navigateTo(ReservationDetailFragment.forId(received.id))
                            }
                        }
                    }
                }
            }
        }
    }
}
