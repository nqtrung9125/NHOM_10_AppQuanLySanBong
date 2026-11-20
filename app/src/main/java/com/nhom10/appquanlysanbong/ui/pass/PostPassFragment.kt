package com.nhom10.appquanlysanbong.ui.pass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentPostPassBinding
import com.nhom10.appquanlysanbong.ui.common.vnd

/** KH-29 Đăng pass sân. */
class PostPassFragment : Fragment(R.layout.fragment_post_pass) {

    private var _binding: FragmentPostPassBinding? = null
    private val binding get() = _binding!!

    private var court: String = "Sân A3 · Cầu lông"
    private var date: String = "Thứ Sáu, 16/10/2026"
    private var time: String = "18:00 – 20:00"
    private var total: Int = 400_000
    private var deposit: Int = 120_000
    private var bookingCode: String = "DS2026101600031"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            court = it.getString(ARG_COURT, court)
            date = it.getString(ARG_DATE, date)
            time = it.getString(ARG_TIME, time)
            total = it.getInt(ARG_TOTAL, total)
            deposit = it.getInt(ARG_DEPOSIT, deposit)
            bookingCode = it.getString(ARG_CODE, bookingCode)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostPassBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Đăng pass sân"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.tvCourt.text = court
        binding.tvDatetime.text = "$date · $time"
        binding.tvBookingCode.text = bookingCode
        binding.tvTotal.text = total.vnd()
        binding.tvDeposit.text = deposit.vnd()
        binding.tvReceiveBack.text = deposit.vnd()

        binding.btnContinue.setOnClickListener {
            val note = binding.etNote.text?.toString().orEmpty()
            navigator.navigateTo(
                ConfirmPostPassFragment.newInstance(
                    court = court,
                    date = date,
                    time = time,
                    total = total,
                    deposit = deposit,
                    bookingCode = bookingCode,
                    note = note
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
            total: Int, deposit: Int, bookingCode: String
        ) = PostPassFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_COURT, court)
                putString(ARG_DATE, date)
                putString(ARG_TIME, time)
                putInt(ARG_TOTAL, total)
                putInt(ARG_DEPOSIT, deposit)
                putString(ARG_CODE, bookingCode)
            }
        }
    }
}
