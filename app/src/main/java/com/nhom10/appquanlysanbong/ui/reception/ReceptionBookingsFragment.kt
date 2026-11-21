package com.nhom10.appquanlysanbong.ui.reception

import android.os.Bundle
import android.view.View
import com.nhom10.appquanlysanbong.data.booking.BookingRepository
import com.nhom10.appquanlysanbong.data.booking.BookingRules
import com.nhom10.appquanlysanbong.data.booking.DepositMethod
import com.nhom10.appquanlysanbong.data.booking.ReservationState
import com.nhom10.appquanlysanbong.data.booking.ScheduleDays
import com.nhom10.appquanlysanbong.data.booking.isStaffExample
import com.nhom10.appquanlysanbong.data.session.DemoAccountRepository
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent
import com.nhom10.appquanlysanbong.ui.common.WorkspaceFragment
import com.nhom10.appquanlysanbong.ui.common.vnd

class ReceptionBookingsFragment : WorkspaceFragment() {
    override val title = "Tra cứu phiếu"
    override val role = Role.RECEPTION
    private var query = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        query = savedInstanceState?.getString("query") ?: arguments?.getString("booking_code").orEmpty()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("query", query)
    }

    override fun WorkspaceContent.content() {
        val repository = BookingRepository(requireContext())
        val search = input("Mã phiếu, số điện thoại, tên sân hoặc khách", query)
        button("Tra cứu") {
            query = search.text.toString().trim()
            render()
        }
        val all = repository.operations()
        val phones = DemoAccountRepository(requireContext()).all().associate { it.id to it.phone }
        val digits = query.filter(Char::isDigit)
        val searchByPhone = digits.isNotEmpty() && query.all { it.isDigit() || it.isWhitespace() }
        val exact = all.firstOrNull { it.id.equals(query, ignoreCase = true) }
        val bookings = if (exact != null) listOf(exact) else all.filter {
            it.id.contains(query, ignoreCase = true) || it.courtName.contains(query, ignoreCase = true) ||
                it.contactName.contains(query, ignoreCase = true) ||
                (searchByPhone && phones[it.ownerId]?.contains(digits) == true)
        }
        if (bookings.isEmpty()) emptyState("Chưa có phiếu phù hợp.")
        val busyCourts = all.filter { it.state == ReservationState.CHECKED_IN }.map { it.courtId }.toSet()
        val now = System.currentTimeMillis()
        bookings.forEach { booking ->
            card(booking.id) {
                badge(booking.state.label)
                if (booking.isStaffExample) badge("Phiếu mẫu")
                valueRow("Khách hàng", booking.contactName)
                phones[booking.ownerId]?.takeIf { it.isNotBlank() }?.let { valueRow("Số điện thoại", it) }
                valueRow("Sân", booking.courtName)
                valueRow("Ngày chơi", booking.dateLabel)
                valueRow("Khung giờ", booking.timeLabel)
                valueRow("Tổng tiền", booking.total.vnd())
                valueRow("Tiền cọc", booking.deposit.vnd())
                booking.depositMethod?.let { valueRow("Hình thức cọc", it.label) }
                when (booking.state) {
                    ReservationState.HELD -> {
                        notice("Hạn giữ đến ${ScheduleDays.deadline(booking.expiresAt)}")
                        button("Xác nhận đã thu cọc", primary = true) {
                            perform("Đã xác nhận tiền cọc.") { repository.payDeposit(booking.id, DepositMethod.COUNTER) }
                        }
                        button("Hủy giữ sân") {
                            confirm("Hủy giữ sân", "Mở lại khung giờ cho khách khác?") {
                                perform("Đã hủy giữ sân.") { repository.cancel(booking.id) }
                            }
                        }
                    }
                    ReservationState.PAID -> when {
                        !BookingRules.isWithinCheckInWindow(booking, now) ->
                            paragraph("Nhận sân từ ${BookingRules.CHECK_IN_EARLY_MINUTES} phút trước giờ chơi đến khi hết giờ.")
                        booking.courtId in busyCourts ->
                            paragraph("Sân vẫn đang thuê. Xác nhận trả sân trước khi nhận lượt này.")
                        else -> button("Nhận sân", primary = true) {
                            perform("Đã xác nhận nhận sân.") { repository.checkIn(booking.id) }
                        }
                    }
                    ReservationState.CHECKED_IN -> {
                        valueRow("Còn phải thu", (booking.total - booking.deposit).vnd())
                        button("Trả sân và thu ${(booking.total - booking.deposit).vnd()}", primary = true) {
                            confirm("Xác nhận đã thu phần còn lại", booking.id) {
                                perform("Đã hoàn tất trả sân.") { repository.checkOut(booking.id) }
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    companion object {
        fun forCode(code: String) = ReceptionBookingsFragment().apply {
            arguments = Bundle().apply { putString("booking_code", code) }
        }
    }
}
