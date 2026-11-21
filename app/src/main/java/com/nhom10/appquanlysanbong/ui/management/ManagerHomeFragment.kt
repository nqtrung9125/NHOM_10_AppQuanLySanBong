package com.nhom10.appquanlysanbong.ui.management

import android.os.Bundle
import android.view.View
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.booking.BookingRepository
import com.nhom10.appquanlysanbong.data.booking.Reservation
import com.nhom10.appquanlysanbong.data.booking.ReservationState
import com.nhom10.appquanlysanbong.data.booking.ScheduleDays
import com.nhom10.appquanlysanbong.data.booking.isStaffExample
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.ui.common.DashboardStat
import com.nhom10.appquanlysanbong.ui.common.StaffAction
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent
import com.nhom10.appquanlysanbong.ui.common.WorkspaceFragment
import com.nhom10.appquanlysanbong.ui.common.actions
import com.nhom10.appquanlysanbong.ui.common.filters
import com.nhom10.appquanlysanbong.ui.common.occupancy
import com.nhom10.appquanlysanbong.ui.common.statistics
import com.nhom10.appquanlysanbong.ui.common.vnd
import com.nhom10.appquanlysanbong.data.session.Role
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ManagerHomeFragment : WorkspaceFragment() {
    override val role = Role.MANAGER
    override val home = true
    protected override val tabs = Page.entries.map { StaffTab(it.label, it.icon) }
    private val page get() = Page.entries[selectedTab]
    override val title get() = page.title
    private var filter = Filter.ALL
    private var refundHistory = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        filter = Filter.entries.firstOrNull { it.name == savedInstanceState?.getString("booking_filter") } ?: filter
        refundHistory = savedInstanceState?.getBoolean("refund_history") ?: refundHistory
        if (session.current.role == role) BookingRepository(requireContext()).ensureStaffExamples()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("booking_filter", filter.name)
        outState.putBoolean("refund_history", refundHistory)
    }

    override fun WorkspaceContent.content() {
        val repository = BookingRepository(requireContext())
        val bookings = repository.operations()
        when (page) {
            Page.OVERVIEW -> overview(bookings)
            Page.BOOKINGS -> bookingList(repository, bookings)
            Page.REFUNDS -> refunds(repository, bookings)
            Page.ACCOUNT -> staffAccount {
                val today = ScheduleDays.date()
                statistics(requireContext(),
                    DashboardStat(bookings.count { it.state == ReservationState.REFUND_REQUESTED }.toString(), "Chờ hoàn cọc"),
                    DashboardStat(bookings.count { it.day == today }.toString(), "Phiếu hôm nay"),
                    DashboardStat(bookings.count { it.state == ReservationState.PASSING }.toString(), "Đang pass")
                )
            }
        }
    }

    private fun WorkspaceContent.overview(bookings: List<Reservation>) {
        val context = requireContext()
        val day = ScheduleDays.date()
        val today = bookings.filter { it.day == day && it.state.occupiesSlot }
        val courts = CourtCatalog(context).all().filter { it.available }
        val peakHours = 16 until 22
        val bookingsByCourt = today.groupBy { it.courtId }
        val usedHours = courts.associateWith { court ->
            val courtBookings = bookingsByCourt[court.id].orEmpty()
            peakHours.count { hour -> courtBookings.any { hour in it.start until it.end } }
        }
        val capacity = courts.size * peakHours.count()
        val percent = if (capacity == 0) 0 else usedHours.values.sum() * 100 / capacity
        val completedValue = today.filter { it.state == ReservationState.COMPLETED }.sumOf { it.total.toLong() }
        val held = bookings.count { it.state == ReservationState.HELD }
        val passing = bookings.count { it.state == ReservationState.PASSING }
        val refunds = bookings.count { it.state == ReservationState.REFUND_REQUESTED }

        heading(SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.forLanguageTag("vi-VN"))
            .format(ScheduleDays.calendar(day).time), size = 15f)
        statistics(context,
            DashboardStat(revenueLabel(completedValue), "Doanh thu", spokenValue = completedValue.vnd()),
            DashboardStat(today.size.toString(), "Phiếu hôm nay"),
            DashboardStat("$percent%", "Lấp đầy"),
            DashboardStat(held.toString(), "Chờ cọc"),
            DashboardStat(refunds.toString(), "Chờ hoàn"),
            DashboardStat(passing.toString(), "Đang pass")
        )
        actions(context, "Cần xử lý",
            StaffAction("Hoàn cọc chờ duyệt · $refunds", R.drawable.ic_check) {
                refundHistory = false
                openTab(Page.REFUNDS.ordinal)
            },
            StaffAction("Chờ thanh toán cọc · $held", R.drawable.ic_history) { showBookings(Filter.HELD) },
            StaffAction("Tin pass chờ nhận · $passing", R.drawable.ic_ql_info) { showBookings(Filter.PASSING) }
        )
        card("Lấp đầy theo sân · 16:00–22:00") {
            if (courts.isEmpty()) paragraph("Chưa có sân đang hoạt động.")
            usedHours.forEach { (court, hours) ->
                occupancy(context, court.name, hours * 100 / peakHours.count())
            }
        }
        button("Xem lịch đặt sân") { showBookings(Filter.ALL) }
    }

    private fun revenueLabel(amount: Long): String {
        val format = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).apply {
            maximumFractionDigits = 1
        }
        return when {
            amount >= 1_000_000 -> "${format.format(amount / 1_000_000.0)} tr"
            amount >= 1_000 -> "${format.format(amount / 1_000.0)}k"
            else -> amount.vnd()
        }
    }

    private fun WorkspaceContent.refunds(repository: BookingRepository, bookings: List<Reservation>) {
        val pending = bookings.filter { it.state == ReservationState.REFUND_REQUESTED }
        filters(requireContext(), listOf("Chờ xử lý (${pending.size})", "Đã xử lý"), if (refundHistory) 1 else 0) {
            refundHistory = it == 1
            render()
        }
        val visible = if (refundHistory) bookings.filter { it.state == ReservationState.REFUNDED } else pending
        bookingRows(repository, visible, if (refundHistory) "Chưa có phiếu đã hoàn cọc." else "Chưa có yêu cầu hoàn cọc.")
    }

    private fun showBookings(target: Filter) {
        filter = target
        openTab(Page.BOOKINGS.ordinal)
    }

    private fun WorkspaceContent.bookingList(repository: BookingRepository, bookings: List<Reservation>) {
        filters(requireContext(), Filter.entries.map { it.label }, filter.ordinal) {
            filter = Filter.entries[it]
            render()
        }
        val day = ScheduleDays.date()
        val result = bookings.filter { booking ->
            when (filter) {
                Filter.ALL -> true
                Filter.TODAY -> booking.day == day
                Filter.HELD -> booking.state == ReservationState.HELD
                Filter.PASSING -> booking.state == ReservationState.PASSING
            }
        }.sortedWith(compareBy(Reservation::day, Reservation::start))
        bookingRows(repository, result, "Chưa có booking trong mục này.")
    }

    private fun WorkspaceContent.bookingRows(
        repository: BookingRepository, bookings: List<Reservation>, empty: String
    ) {
        if (bookings.isEmpty()) emptyState(empty)
        bookings.forEach { booking ->
            card(booking.courtName) {
                badge(booking.state.label)
                if (booking.isStaffExample) badge("Phiếu mẫu")
                valueRow("Khách hàng", booking.contactName)
                valueRow("Khung giờ", "${booking.dateLabel} · ${booking.timeLabel}")
                valueRow("Mã phiếu", booking.id)
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

    private enum class Page(val label: String, val title: String, val icon: Int) {
        OVERVIEW("Tổng quan", "Tổng quan hôm nay", R.drawable.ic_home),
        BOOKINGS("Lịch đặt", "Lịch đặt sân", R.drawable.ic_history),
        REFUNDS("Hoàn cọc", "Xử lý hoàn cọc", R.drawable.ic_check),
        ACCOUNT("Cá nhân", "Tài khoản quản lý", R.drawable.ic_person)
    }

    private enum class Filter(val label: String) {
        ALL("Tất cả"), TODAY("Hôm nay"), HELD("Chờ cọc"), PASSING("Đang pass")
    }
}
