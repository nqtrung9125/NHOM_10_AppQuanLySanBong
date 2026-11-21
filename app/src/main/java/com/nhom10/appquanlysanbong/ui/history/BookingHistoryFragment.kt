package com.nhom10.appquanlysanbong.ui.history

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.booking.BookingRepository
import com.nhom10.appquanlysanbong.data.booking.Reservation
import com.nhom10.appquanlysanbong.data.booking.ReservationState
import com.nhom10.appquanlysanbong.data.booking.ScheduleDays
import com.nhom10.appquanlysanbong.databinding.FragmentBookingHistoryBinding
import com.nhom10.appquanlysanbong.databinding.ItemBookingBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab
import com.nhom10.appquanlysanbong.ui.common.setChipSelected
import com.nhom10.appquanlysanbong.ui.common.tintBackground
import com.nhom10.appquanlysanbong.ui.common.vnd
import com.nhom10.appquanlysanbong.ui.booking.ReservationDetailFragment

/** KH-08 Lịch sử đặt sân: lọc theo trạng thái, các nút hành động từng thẻ. */
class BookingHistoryFragment : Fragment(R.layout.fragment_booking_history) {

    private var _binding: FragmentBookingHistoryBinding? = null
    private val binding get() = _binding!!

    private var filter = 0
    private var chips = emptyList<TextView>()

    // Thứ tự khớp với chips: Tất cả, Chờ cọc, Đã cọc, Đã hủy
    private val filters = listOf<(ReservationState) -> Boolean>(
        { true },
        { it == ReservationState.HELD },
        { it in setOf(ReservationState.PAID, ReservationState.CHECKED_IN, ReservationState.COMPLETED, ReservationState.PASSING) },
        { it in setOf(ReservationState.CANCELLED, ReservationState.REFUND_REQUESTED, ReservationState.REFUNDED, ReservationState.EXPIRED) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookingHistoryBinding.bind(view)

        binding.appbar.tvAppbarTitle.setText(R.string.history_title)
        binding.appbar.btnBack.visibility = View.GONE // tab gốc, không có nút back

        chips = listOf(
            binding.chipHAll, binding.chipHPending, binding.chipHPaid, binding.chipHCancelled
        )
        chips.forEachIndexed { i, chip ->
            chip.setOnClickListener {
                filter = i
                render()
            }
        }

        BottomNav.setup(binding.bottomNav, NavTab.HISTORY, requireActivity() as Navigator)
        render()
    }

    private fun render() {
        chips.forEachIndexed { i, chip -> chip.setChipSelected(i == filter) }

        val list = BookingRepository(requireContext()).mine().filter { filters[filter](it.state) }
        binding.llBookings.removeAllViews()
        list.forEach { binding.llBookings.addView(createCard(it)) }
        binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun createCard(b: Reservation): View {
        val ctx = requireContext()
        val card = ItemBookingBinding.inflate(layoutInflater, binding.llBookings, false)

        card.tvBookingCourt.text = b.courtName
        card.tvBookingDate.text = b.dateLabel
        card.tvBookingTime.text = b.timeLabel
        card.tvBookingTotal.text = b.total.vnd()
        card.tvBookingStatus.text = b.state.label
        val pending = b.state == ReservationState.HELD || b.state == ReservationState.REFUND_REQUESTED
        card.tvBookingStatus.tintBackground(if (pending) R.color.warning_bg else R.color.brand_tint)
        card.tvBookingStatus.setTextColor(ContextCompat.getColor(ctx, if (pending) R.color.warning_text else R.color.brand))
        card.tvBookingNote.text = if (b.state == ReservationState.HELD) {
            "Giữ đến ${ScheduleDays.deadline(b.expiresAt)}"
        } else b.id
        val openDetail = View.OnClickListener {
            (requireActivity() as Navigator).navigateTo(ReservationDetailFragment.forId(b.id))
        }
        if (b.state == ReservationState.HELD) {
            card.btnPay.visibility = View.VISIBLE
            card.btnPay.text = getString(R.string.pay_deposit, b.deposit.vnd())
            card.btnPay.setOnClickListener(openDetail)
        }
        if (b.state == ReservationState.PAID && ScheduleDays.startMillis(b.day, b.start) > System.currentTimeMillis()) {
            card.btnPass.visibility = View.VISIBLE
            card.btnPass.setOnClickListener(openDetail)
        }
        card.root.setOnClickListener(openDetail)
        return card.root
    }

    override fun onDestroyView() {
        chips = emptyList()
        _binding = null
        super.onDestroyView()
    }
}
