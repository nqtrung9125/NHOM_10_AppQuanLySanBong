package com.nhom10.appquanlysanbong.ui.booking

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.Court
import com.nhom10.appquanlysanbong.data.booking.BookingRepository
import com.nhom10.appquanlysanbong.data.booking.BookingRules
import com.nhom10.appquanlysanbong.data.booking.ScheduleDays
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.data.courts.displayName
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.data.session.SessionStore
import com.nhom10.appquanlysanbong.ui.auth.LoginFragment
import com.nhom10.appquanlysanbong.databinding.FragmentBookingBinding
import com.nhom10.appquanlysanbong.databinding.ItemDayBinding
import com.nhom10.appquanlysanbong.databinding.ItemSlotBinding
import com.nhom10.appquanlysanbong.ui.common.tintBackground
import com.nhom10.appquanlysanbong.ui.common.vnd
import java.util.Locale
import java.util.Calendar
import java.text.SimpleDateFormat

/** KH-06 Đặt sân: chọn ngày, chọn khung giờ liền nhau, xem tóm tắt tiền và cọc 30%. */
class BookingFragment : Fragment(R.layout.fragment_booking) {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private lateinit var court: Court
    private lateinit var repository: BookingRepository
    private var dayIndex = 0
    private val days = (0..6).map { ScheduleDays.date(it) }
    private val selected = sortedSetOf<Int>() // giờ bắt đầu của các khung đang chọn
    private val dayViews = mutableListOf<ItemDayBinding>()
    private val slotViews = linkedMapOf<Int, TextView>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookingBinding.bind(view)
        court = CourtCatalog(requireContext()).find(arguments?.getString(ARG_ID)) ?: return
        repository = BookingRepository(requireContext())

        val isGuest = SessionStore(requireContext()).current.role == Role.GUEST
        binding.appbar.tvAppbarTitle.text = if (isGuest) "Lịch trống · ${court.name}"
            else getString(R.string.booking_title, court.name)
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.rowCourt.tvRowLabel.setText(R.string.summary_court)
        binding.rowDate.tvRowLabel.setText(R.string.summary_date)
        binding.rowTime.tvRowLabel.setText(R.string.summary_time)
        binding.rowTotal.tvRowLabel.setText(R.string.summary_total)
        binding.rowDeposit.tvRowLabel.text =
            getString(R.string.summary_deposit, BookingRules.DEPOSIT_PERCENT)
        binding.rowRemain.tvRowLabel.setText(R.string.summary_remain)

        // Dòng "Cọc cần thanh toán" nổi bật hơn các dòng khác
        binding.rowDeposit.tvRowLabel.textSize = 15f
        binding.rowDeposit.tvRowValue.textSize = 20f
        binding.rowDeposit.tvRowValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand))
        binding.rowDeposit.tvRowValue.setTypeface(Typeface.DEFAULT_BOLD)

        binding.note.tvNoteText.text = "Khung xám đã qua giờ hoặc không còn trống. Giữ sân tối đa 15 phút để thanh toán cọc."
        val navigator = requireActivity() as Navigator
        if (isGuest) {
            binding.btnConfirm.text = "Đăng nhập để đặt sân"
            binding.btnConfirm.setOnClickListener { navigator.navigateTo(LoginFragment()) }
        } else {
            binding.btnConfirm.text = "Giữ sân và thanh toán cọc"
            binding.btnConfirm.setOnClickListener { confirm() }
        }

        buildDays()
        buildSlots()
        renderDays()
        renderSlots()
        renderSummary()
    }

    private fun buildDays() {
        dayViews.clear()
        days.forEachIndexed { i, day ->
            val item = ItemDayBinding.inflate(layoutInflater, binding.llDays, false)
            val date = ScheduleDays.calendar(day)
            val weekday = date.get(Calendar.DAY_OF_WEEK)
            item.tvWeekday.text = if (weekday == Calendar.SUNDAY) "CN" else "T$weekday"
            item.tvDayNumber.text = day.takeLast(2)
            if (i > 0) (item.root.layoutParams as LinearLayout.LayoutParams).marginStart = dp(5)
            item.root.setOnClickListener {
                dayIndex = i
                selected.clear()
                renderDays()
                renderSlots()
                renderSummary()
            }
            binding.llDays.addView(item.root)
            dayViews.add(item)
        }
    }

    private fun buildSlots() {
        slotViews.clear()
        (5..21).toList().chunked(3).forEach { hours ->
            val row = LinearLayout(requireContext()).apply { orientation = LinearLayout.HORIZONTAL }
            binding.llSlots.addView(row, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(8) })
            hours.forEachIndexed { index, hour ->
                val tv = ItemSlotBinding.inflate(layoutInflater, row, false).root
                tv.text = String.format(Locale.getDefault(), "%02d:00 – %02d:00", hour, hour + 1)
                if (index != 0) (tv.layoutParams as LinearLayout.LayoutParams).marginStart = dp(8)
                tv.setOnClickListener { onSlotClick(hour) }
                row.addView(tv)
                slotViews[hour] = tv
            }
        }
    }

    private fun renderDays() {
        val ctx = requireContext()
        dayViews.forEachIndexed { i, item ->
            val on = i == dayIndex
            item.root.tintBackground(if (on) R.color.brand else null)
            item.tvWeekday.setTextColor(
                ContextCompat.getColor(ctx, if (on) R.color.brand_tint else R.color.text_muted)
            )
            item.tvDayNumber.setTextColor(
                ContextCompat.getColor(ctx, if (on) android.R.color.white else R.color.text_primary)
            )
        }
    }

    private fun renderSlots() {
        val ctx = requireContext()
        val available = repository.availableHours(court.id, days[dayIndex])
        if (selected.any { it !in available }) selected.clear()
        slotViews.forEach { (hour, tv) ->
            tv.isEnabled = hour in available
            when {
                hour !in available -> {
                    tv.tintBackground(R.color.border_card)
                    tv.setTextColor(ContextCompat.getColor(ctx, R.color.text_hint))
                    tv.typeface = Typeface.DEFAULT
                }
                hour in selected -> {
                    tv.tintBackground(R.color.brand)
                    tv.setTextColor(ContextCompat.getColor(ctx, android.R.color.white))
                    tv.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                }
                else -> {
                    tv.tintBackground(null)
                    tv.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary))
                    tv.typeface = Typeface.DEFAULT
                }
            }
        }
    }

    private fun onSlotClick(hour: Int) {
        if (!available(hour)) return
        when {
            hour in selected -> {
                // Bấm đầu/cuối dải thì bỏ khung đó, bấm giữa dải thì chọn lại từ đầu
                if (hour == selected.first() || hour == selected.last()) {
                    selected.remove(hour)
                } else {
                    selected.clear()
                    selected.add(hour)
                }
            }
            selected.isEmpty() -> selected.add(hour)
            hour == selected.last() + 1 || hour == selected.first() - 1 -> selected.add(hour)
            else -> {
                selected.clear()
                selected.add(hour)
            }
        }
        renderSlots()
        renderSummary()
    }

    private fun available(hour: Int) = repository.isFree(court.id, days[dayIndex], hour, hour + 1)

    private fun timeRange(): String =
        String.format(Locale.getDefault(), "%02d:00 – %02d:00", selected.first(), selected.last() + 1)

    private fun renderSummary() {
        val day = days[dayIndex]
        binding.rowCourt.tvRowValue.text = court.displayName
        binding.rowDate.tvRowValue.text = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.forLanguageTag("vi-VN"))
            .format(ScheduleDays.calendar(day).time)

        if (selected.isEmpty()) {
            binding.rowTime.tvRowValue.text = "—"
            binding.rowTotal.tvRowValue.text = 0.vnd()
            binding.rowDeposit.tvRowValue.text = 0.vnd()
            binding.rowRemain.tvRowValue.text = 0.vnd()
            return
        }
        val total = BookingRules.quote(court, day, selected.first(), selected.last() + 1)
        val deposit = BookingRules.deposit(total)
        binding.rowTime.tvRowValue.text =
            getString(R.string.time_range_hours, timeRange(), selected.size)
        binding.rowTotal.tvRowValue.text = total.vnd()
        binding.rowDeposit.tvRowValue.text = deposit.vnd()
        binding.rowRemain.tvRowValue.text = (total - deposit).vnd()
    }

    private fun confirm() {
        if (selected.isEmpty()) {
            Toast.makeText(requireContext(), R.string.msg_pick_slot, Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val booking = repository.hold(court.id, days[dayIndex], selected.first(), selected.last() + 1)
            (requireActivity() as Navigator).navigateTo(ReservationDetailFragment.forId(booking.id))
        } catch (error: IllegalArgumentException) {
            Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
            renderSlots()
            renderSummary()
        }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        dayViews.clear()
        slotViews.clear()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_ID = "court_id"

        fun newInstance(courtId: String) = BookingFragment().apply {
            arguments = Bundle().apply { putString(ARG_ID, courtId) }
        }
    }
}
