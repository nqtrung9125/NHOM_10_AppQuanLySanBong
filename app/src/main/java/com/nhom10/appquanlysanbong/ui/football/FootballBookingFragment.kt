package com.nhom10.appquanlysanbong.ui.football

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentFootballBookingBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab
import com.nhom10.appquanlysanbong.ui.common.tintBackground

/** KH-34 Đặt sân bóng đá & chọn khung giờ. */
class FootballBookingFragment : Fragment(R.layout.fragment_football_booking) {

    private var _binding: FragmentFootballBookingBinding? = null
    private val binding get() = _binding!!

    private var courtName: String = "Sân D1 · Bóng đá 5 người"
    private var selectedSlot = 1 // 0: 17h, 1: 18h (selected), 2: 19h

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courtName = arguments?.getString(ARG_COURT, courtName) ?: courtName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFootballBookingBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Đặt sân D1"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        BottomNav.setup(binding.bottomNav, NavTab.HOME, navigator)
        binding.tvSummaryCourt.text = courtName

        binding.slot17.setOnClickListener { selectSlot(0) }
        binding.slot18.setOnClickListener { selectSlot(1) }
        binding.slot19.setOnClickListener { selectSlot(2) }

        selectSlot(selectedSlot)

        binding.btnConfirmPay.setOnClickListener {
            navigator.navigateTo(
                FootballPaymentFragment.newInstance(
                    court = courtName,
                    date = "20/11/2026",
                    time = "18:00 – 19:00",
                    total = 500_000,
                    deposit = 150_000,
                    code = "DS2026112000042"
                )
            )
        }
    }

    private fun selectSlot(index: Int) {
        selectedSlot = index
        val ctx = requireContext()
        val textPrimary = ContextCompat.getColor(ctx, R.color.text_primary)
        val white = ContextCompat.getColor(ctx, android.R.color.white)

        binding.slot17.tintBackground(if (index == 0) R.color.brand else null)
        binding.slot17.setTextColor(if (index == 0) white else textPrimary)
        binding.slot17.typeface = if (index == 0) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        binding.slot18.tintBackground(if (index == 1) R.color.brand else null)
        binding.slot18.setTextColor(if (index == 1) white else textPrimary)
        binding.slot18.typeface = if (index == 1) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        binding.slot19.tintBackground(if (index == 2) R.color.brand else null)
        binding.slot19.setTextColor(if (index == 2) white else textPrimary)
        binding.slot19.typeface = if (index == 2) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        val timeStr = when (index) {
            0 -> "17:00 – 18:00"
            1 -> "18:00 – 19:00"
            else -> "19:00 – 20:00"
        }
        binding.tvSummaryTime.text = "Đã chọn $timeStr"
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_COURT = "court_name"
        fun newInstance(courtName: String) = FootballBookingFragment().apply {
            arguments = Bundle().apply { putString(ARG_COURT, courtName) }
        }
    }
}
