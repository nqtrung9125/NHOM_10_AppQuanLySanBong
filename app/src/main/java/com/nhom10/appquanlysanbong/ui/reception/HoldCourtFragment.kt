package com.nhom10.appquanlysanbong.ui.reception

import com.nhom10.appquanlysanbong.data.booking.BookingRepository
import com.nhom10.appquanlysanbong.data.booking.ScheduleDays
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.data.courts.displayName
import com.nhom10.appquanlysanbong.data.session.DemoAccountRepository
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent
import com.nhom10.appquanlysanbong.ui.common.WorkspaceFragment

class HoldCourtFragment : WorkspaceFragment() {
    override val title = "Giữ sân cho khách"
    override val role = Role.RECEPTION

    override fun WorkspaceContent.content() {
        val courts = CourtCatalog(requireContext()).all().filter { it.available }
        if (courts.isEmpty()) {
            paragraph("Chưa có sân đang hoạt động.")
            return
        }
        val customers = DemoAccountRepository(requireContext()).all().filter { it.role == Role.CUSTOMER }
        val owner = choice("Khách hàng", customers.map { "${it.name} · ${it.phone}" })
        val court = choice("Sân", courts.map { it.displayName })
        val dates = (0..6).map { ScheduleDays.date(it) }
        val day = choice("Ngày chơi", dates.map { it.split('-').asReversed().joinToString("/") })
        val start = choice("Bắt đầu", (5..21).map { "%02d:00".format(it) }, 13)
        val duration = choice("Thời lượng", listOf("1 giờ", "2 giờ", "3 giờ"))
        notice("Chỉ giữ khung giờ còn trống. Giữ sân chưa phải là xác nhận đã thu cọc.")
        button("Giữ sân 15 phút", primary = true) {
            perform("Đã giữ sân cho khách.") {
                val booking = BookingRepository(requireContext()).hold(
                    courts[court.selectedItemPosition].id, dates[day.selectedItemPosition],
                    start.selectedItemPosition + 5, start.selectedItemPosition + 6 + duration.selectedItemPosition,
                    customers[owner.selectedItemPosition]
                )
                parentFragmentManager.popBackStack()
                navigator.navigateTo(ReceptionBookingsFragment.forCode(booking.id))
            }
        }
    }
}
