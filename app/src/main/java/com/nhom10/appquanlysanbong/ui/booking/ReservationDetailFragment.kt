package com.nhom10.appquanlysanbong.ui.booking

import android.os.Bundle
import com.nhom10.appquanlysanbong.data.booking.BookingRepository
import com.nhom10.appquanlysanbong.data.booking.BookingRules
import com.nhom10.appquanlysanbong.data.booking.DepositMethod
import com.nhom10.appquanlysanbong.data.booking.ReservationState
import com.nhom10.appquanlysanbong.data.booking.ScheduleDays
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent
import com.nhom10.appquanlysanbong.ui.common.WorkspaceFragment
import com.nhom10.appquanlysanbong.ui.common.vnd
import com.nhom10.appquanlysanbong.ui.history.BookingHistoryFragment

/** Đọc theo mã booking để lịch sử và màn thanh toán luôn hiển thị bản ghi hiện tại. */
class ReservationDetailFragment : WorkspaceFragment() {
    override val title = "Chi tiết đặt sân"
    override val role = Role.CUSTOMER

    override fun WorkspaceContent.content() {
        val repo = BookingRepository(requireContext())
        val booking = repo.mine().find { it.id == arguments?.getString("booking_id") }
        if (booking == null) {
            emptyState("Không tìm thấy phiếu đặt sân trong tài khoản này.")
            return
        }
        card(booking.courtName) {
            badge(booking.state.label)
            valueRow("Mã phiếu", booking.id)
            valueRow("Ngày chơi", booking.dateLabel)
            valueRow("Khung giờ", booking.timeLabel)
            valueRow("Tổng tiền", booking.total.vnd())
            valueRow("Tiền cọc", booking.deposit.vnd())
            valueRow("Còn trả tại sân", (booking.total - booking.deposit).vnd())
            booking.depositMethod?.let { valueRow("Hình thức cọc", it.label) }
        }
        val beginsAt = ScheduleDays.startMillis(booking.day, booking.start)
        val remaining = beginsAt - System.currentTimeMillis()
        when (booking.state) {
            ReservationState.HELD -> {
                card("Thanh toán cọc") {
                    paragraph("Đang giữ đến ${ScheduleDays.deadline(booking.expiresAt)}.")
                    val methods = listOf(DepositMethod.TRANSFER, DepositMethod.WALLET, DepositMethod.CARD)
                    val method = choice("Phương thức demo", methods.map { it.label })
                    button("Thanh toán cọc demo ${booking.deposit.vnd()}", primary = true) {
                        perform("Đã thanh toán cọc demo.") {
                            repo.payDeposit(booking.id, methods[method.selectedItemPosition])
                        }
                    }
                }
                button("Hủy giữ sân") {
                    confirm("Hủy giữ sân", "Khung giờ sẽ mở lại cho khách khác.") {
                        perform("Đã hủy giữ sân.") { repo.cancel(booking.id) }
                    }
                }
            }
            ReservationState.PAID -> {
                notice("Đọc mã phiếu cho lễ tân khi đến nhận sân.")
                if (remaining > 0) {
                    val price = input("Giá pass (đ)", booking.deposit.toString(), numeric = true)
                    button("Đăng pass sân", primary = true) {
                        perform("Đã đăng tin pass sân.") {
                            repo.postPass(booking.id, price.text.toString().toIntOrNull() ?: 0)
                        }
                    }
                    val refund = remaining >= BookingRules.REFUND_HOURS * 3_600_000L
                    button(if (refund) "Yêu cầu hủy và hoàn cọc" else "Hủy booking") {
                        confirm("Hủy booking", if (refund) "Quản lý sẽ xử lý hoàn cọc." else
                            "Đặt sân còn dưới 12 giờ. Khi hủy, tiền cọc sẽ không được hoàn.") {
                            perform("Đã cập nhật booking.") { repo.cancel(booking.id) }
                        }
                    }
                }
            }
            ReservationState.PASSING -> {
                paragraph("Giá pass ${booking.passPrice.vnd()}. Người nhận sẽ tiếp quản booking này.")
                button("Gỡ tin pass") { perform("Đã gỡ tin pass.") { repo.withdrawPass(booking.id) } }
            }
            ReservationState.REFUND_REQUESTED -> paragraph("Đang chờ quản lý duyệt hoàn tiền cọc demo.")
            else -> Unit
        }
        button("Về lịch sử đặt sân") { navigator.navigateAsRoot(BookingHistoryFragment()) }
    }

    companion object {
        fun forId(id: String) = ReservationDetailFragment().apply {
            arguments = Bundle().apply { putString("booking_id", id) }
        }
    }
}
