package com.nhom10.appquanlysanbong.ui.football

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentFootballPaymentBinding
import com.nhom10.appquanlysanbong.ui.booking.BookingSuccessFragment
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab
import com.nhom10.appquanlysanbong.ui.common.vnd

/** KH-35 Thanh toán tiền cọc bóng đá. */
class FootballPaymentFragment : Fragment(R.layout.fragment_football_payment) {

    private var _binding: FragmentFootballPaymentBinding? = null
    private val binding get() = _binding!!

    private var court: String = "Sân D1 · Bóng đá 5 người"
    private var date: String = "20/11/2026"
    private var time: String = "18:00 – 19:00"
    private var total: Int = 500_000
    private var deposit: Int = 150_000
    private var code: String = "DS2026112000042"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            court = it.getString(ARG_COURT, court)
            date = it.getString(ARG_DATE, date)
            time = it.getString(ARG_TIME, time)
            total = it.getInt(ARG_TOTAL, total)
            deposit = it.getInt(ARG_DEPOSIT, deposit)
            code = it.getString(ARG_CODE, code)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFootballPaymentBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Thanh toán tiền cọc"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        BottomNav.setup(binding.bottomNav, NavTab.HOME, navigator)

        binding.tvCourt.text = court
        binding.tvDatetime.text = "$date · $time"
        binding.tvBookingCode.text = code
        binding.tvDepositHeading.text = "Cọc cần thanh toán: ${deposit.vnd()}"
        binding.btnPayDeposit.text = "Thanh toán ${deposit.vnd()}"

        binding.btnCancelHold.setOnClickListener {
            Toast.makeText(requireContext(), "Đã hủy giữ chỗ", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnPayDeposit.setOnClickListener {
            navigator.navigateTo(
                BookingSuccessFragment.newInstance(
                    court = court,
                    date = date,
                    time = time,
                    total = total,
                    deposit = deposit,
                    code = code
                )
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_COURT = "court"
        private const val ARG_DATE = "date"
        private const val ARG_TIME = "time"
        private const val ARG_TOTAL = "total"
        private const val ARG_DEPOSIT = "deposit"
        private const val ARG_CODE = "code"

        fun newInstance(
            court: String, date: String, time: String,
            total: Int, deposit: Int, code: String
        ) = FootballPaymentFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_COURT, court)
                putString(ARG_DATE, date)
                putString(ARG_TIME, time)
                putInt(ARG_TOTAL, total)
                putInt(ARG_DEPOSIT, deposit)
                putString(ARG_CODE, code)
            }
        }
    }
}
