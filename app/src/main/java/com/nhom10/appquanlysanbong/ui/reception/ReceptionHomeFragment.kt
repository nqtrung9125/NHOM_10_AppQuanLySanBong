package com.nhom10.appquanlysanbong.ui.reception

import android.os.Bundle
import android.view.View
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.booking.BookingRepository
import com.nhom10.appquanlysanbong.data.booking.Reservation
import com.nhom10.appquanlysanbong.data.booking.ReservationState
import com.nhom10.appquanlysanbong.data.booking.ScheduleDays
import com.nhom10.appquanlysanbong.data.booking.isStaffExample
import com.nhom10.appquanlysanbong.data.matchmaking.MatchRequestState
import com.nhom10.appquanlysanbong.data.matchmaking.MatchmakingRepository
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.ui.common.DashboardStat
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent
import com.nhom10.appquanlysanbong.ui.common.WorkspaceFragment
import com.nhom10.appquanlysanbong.ui.common.statistics

class ReceptionHomeFragment : WorkspaceFragment() {
    override val role = Role.RECEPTION
    override val home = true
    protected override val tabs = Page.entries.map { StaffTab(it.label, it.icon) }
    private val page get() = Page.entries[selectedTab]
    override val title get() = page.title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (session.current.role == role) {
            BookingRepository(requireContext()).ensureStaffExamples()
            MatchmakingRepository(requireContext()).ensureStaffExamples()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun WorkspaceContent.content() {
        when (page) {
            Page.RENTALS -> rentals()
            Page.LOOKUP -> bookingSearch()
            Page.OPERATIONS -> operations()
            Page.ACCOUNT -> staffAccount {
                val context = requireContext()
                val today = ScheduleDays.date()
                val bookings = BookingRepository(context).operations().filter { it.day == today }
                statistics(context,
                    DashboardStat(bookings.size.toString(), "Phiếu hôm nay"),
                    DashboardStat(bookings.count { it.state == ReservationState.CHECKED_IN }.toString(), "Đang thuê"),
                    DashboardStat(bookings.count { it.state == ReservationState.COMPLETED }.toString(), "Đã trả sân")
                )
            }
        }
    }

    private fun WorkspaceContent.rentals() {
        val bookings = BookingRepository(requireContext()).operations()
        val now = System.currentTimeMillis()
        val renting = bookings.filter { it.state == ReservationState.CHECKED_IN }
            .sortedWith(compareBy({ it.day }, { it.end }))
        val upcoming = bookings.filter {
            it.state == ReservationState.PAID && ScheduleDays.startMillis(it.day, it.end) > now
        }.sortedWith(compareBy(Reservation::day, Reservation::start))
        val ending = renting.count { ScheduleDays.startMillis(it.day, it.end) - now <= 15 * 60_000L }
        if (ending > 0) notice("$ending sân sắp hoặc đã hết giờ. Mở phiếu để xác nhận trả sân.")
        statistics(
            requireContext(),
            DashboardStat(renting.size.toString(), "Đang thuê"),
            DashboardStat(upcoming.size.toString(), "Sắp tới"),
            DashboardStat(ending.toString(), "Sắp hết giờ")
        )
        if (renting.isEmpty()) emptyState("Hiện chưa có sân nào đang thuê.")
        renting.forEach { reservationCard(it, "Mở phiếu trả sân") }
        button("Giữ sân và cáp kèo") { openTab(Page.OPERATIONS.ordinal) }
        heading("Sắp tới")
        if (upcoming.isEmpty()) emptyState("Chưa có phiếu đang chờ nhận sân.")
        upcoming.forEach { reservationCard(it, "Mở phiếu nhận sân") }
    }

    private fun WorkspaceContent.bookingSearch() {
        val query = input("Mã phiếu, số điện thoại, tên sân hoặc khách")
        button("Tra cứu", primary = true) {
            val code = query.text.toString().trim()
            navigator.navigateTo(ReceptionBookingsFragment.forCode(code))
        }
        val held = BookingRepository(requireContext()).operations().filter { it.state == ReservationState.HELD }
        heading("Đang giữ sân")
        if (held.isEmpty()) emptyState("Chưa có phiếu đang giữ sân.")
        held.forEach { reservationCard(it, "Mở phiếu ${it.id}") }
    }

    private fun WorkspaceContent.operations() {
        val context = requireContext()
        val held = BookingRepository(context).operations().count { it.state == ReservationState.HELD }
        val waiting = MatchmakingRepository(context).requests().count { it.state == MatchRequestState.WAITING }
        notice("$held chỗ đang giữ · $waiting yêu cầu cáp kèo đang chờ")
        card("Giữ sân cho khách") {
            badge("Giữ tạm trước khi thu cọc")
            paragraph("Kiểm tra giờ trống và giữ sân cho khách trong 15 phút.")
            button("Tạo giữ sân", primary = true) { navigator.navigateTo(HoldCourtFragment()) }
            button("Xem phiếu giữ sân") { openTab(Page.LOOKUP.ordinal) }
        }
        card("Cáp kèo / Tìm bạn chơi") {
            badge("$waiting yêu cầu đang chờ")
            paragraph("Lọc theo môn và gửi đề xuất cho khách.")
            button("Xem yêu cầu cáp kèo") { navigator.navigateTo(ReceptionMatchmakingFragment()) }
        }
    }

    private fun WorkspaceContent.reservationCard(booking: Reservation, label: String) {
        card(booking.courtName) {
            badge(booking.state.label)
            if (booking.isStaffExample) badge("Phiếu mẫu")
            valueRow("Khách hàng", booking.contactName)
            valueRow("Khung giờ", "${booking.dateLabel} · ${booking.timeLabel}")
            button(label) { navigator.navigateTo(ReceptionBookingsFragment.forCode(booking.id)) }
        }
    }

    private enum class Page(val label: String, val title: String, val icon: Int) {
        RENTALS("Đang thuê", "Sân đang thuê", R.drawable.ic_lt_nav_rentals),
        LOOKUP("Tra cứu", "Tra cứu phiếu", R.drawable.ic_lt_nav_lookup),
        OPERATIONS("Nghiệp vụ", "Nghiệp vụ lễ tân", R.drawable.ic_lt_nav_operations),
        ACCOUNT("Cá nhân", "Tài khoản lễ tân", R.drawable.ic_lt_nav_account)
    }
}
