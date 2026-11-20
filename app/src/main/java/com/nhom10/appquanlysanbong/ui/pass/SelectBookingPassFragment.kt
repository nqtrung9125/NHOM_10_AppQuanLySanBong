package com.nhom10.appquanlysanbong.ui.pass

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.databinding.FragmentSelectBookingPassBinding
import com.nhom10.appquanlysanbong.databinding.ItemSelectBookingCardBinding
import com.nhom10.appquanlysanbong.ui.common.vnd

/** KH-28 Chọn booking để đăng pass. */
class SelectBookingPassFragment : Fragment(R.layout.fragment_select_booking_pass) {

    private var _binding: FragmentSelectBookingPassBinding? = null
    private val binding get() = _binding!!

    private var selectedIndex = 0
    private val cardBindings = mutableListOf<ItemSelectBookingCardBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSelectBookingPassBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Chọn booking để pass"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        renderBookings()

        binding.btnContinue.setOnClickListener {
            val selectedBooking = DemoData.bookings[selectedIndex]
            navigator.navigateTo(
                PostPassFragment.newInstance(
                    court = selectedBooking.court,
                    date = selectedBooking.date,
                    time = selectedBooking.time,
                    total = 400_000,
                    deposit = 120_000,
                    bookingCode = "DS2026101600031"
                )
            )
        }
    }

    private fun renderBookings() {
        binding.llBookingList.removeAllViews()
        cardBindings.clear()

        val eligibleBookings = DemoData.bookings.take(3)
        eligibleBookings.forEachIndexed { index, b ->
            val cardBinding = ItemSelectBookingCardBinding.inflate(layoutInflater, binding.llBookingList, false)
            cardBinding.tvCourtName.text = b.court
            cardBinding.tvStatusBadge.text = b.status.label
            cardBinding.tvDate.text = b.date
            cardBinding.tvTime.text = b.time
            cardBinding.tvTotal.text = b.total
            cardBinding.tvDetailNote.text = b.note

            if (index == 2) {
                cardBinding.tvStatusBadge.text = "Chờ thanh toán cọc"
                cardBinding.tvStatusBadge.setBackgroundColor(0xFFFEF3C7.toInt())
                cardBinding.tvStatusBadge.setTextColor(0xFFD97706.toInt())
                cardBinding.tvDetailNote.text = "Chưa thanh toán cọc · Không thể pass"
                cardBinding.tvDetailNote.setTextColor(0xFFDC2626.toInt())
                cardBinding.cardRoot.alpha = 0.6f
            } else {
                cardBinding.cardRoot.setOnClickListener {
                    selectedIndex = index
                    renderSelection()
                }
            }

            cardBindings.add(cardBinding)
            binding.llBookingList.addView(cardBinding.root)
        }
        renderSelection()
    }

    private fun renderSelection() {
        cardBindings.forEachIndexed { i, cb ->
            cb.radioSelect.isChecked = (i == selectedIndex)
            cb.cardRoot.setBackgroundResource(if (i == selectedIndex && i != 2) R.drawable.bg_card else R.drawable.bg_card)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
