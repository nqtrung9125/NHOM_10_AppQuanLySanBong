package com.nhom10.appquanlysanbong.ui.pass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentConfirmPostPassBinding
import com.nhom10.appquanlysanbong.ui.common.vnd

/** KH-30 Xác nhận đăng pass sân. */
class ConfirmPostPassFragment : Fragment(R.layout.fragment_confirm_post_pass) {

    private var _binding: FragmentConfirmPostPassBinding? = null
    private val binding get() = _binding!!

    private var court: String = "Sân A3 · Cầu lông"
    private var date: String = "Thứ Sáu, 16/10/2026"
    private var time: String = "18:00 – 20:00 (2 giờ)"
    private var total: Int = 400_000
    private var deposit: Int = 120_000
    private var bookingCode: String = "DS2026101600031"
    private var note: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            court = it.getString(ARG_COURT, court)
            date = it.getString(ARG_DATE, date)
            time = it.getString(ARG_TIME, time)
            total = it.getInt(ARG_TOTAL, total)
            deposit = it.getInt(ARG_DEPOSIT, deposit)
            bookingCode = it.getString(ARG_CODE, bookingCode)
            note = it.getString(ARG_NOTE, note)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentConfirmPostPassBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Xác nhận đăng pass"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.tvCourt.text = court
        binding.tvDate.text = date
        binding.tvTime.text = time
        binding.tvTotal.text = total.vnd()
        binding.tvDeposit.text = deposit.vnd()

        binding.btnConfirmPost.setOnClickListener {
            navigator.navigateTo(
                PostPassSuccessFragment.newInstance(
                    court = court,
                    date = date,
                    time = time,
                    bookingCode = bookingCode
                )
            )
        }
        binding.btnEdit.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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
        private const val ARG_NOTE = "note"

        fun newInstance(
            court: String, date: String, time: String,
            total: Int, deposit: Int, bookingCode: String, note: String
        ) = ConfirmPostPassFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_COURT, court)
                putString(ARG_DATE, date)
                putString(ARG_TIME, time)
                putInt(ARG_TOTAL, total)
                putInt(ARG_DEPOSIT, deposit)
                putString(ARG_CODE, bookingCode)
                putString(ARG_NOTE, note)
            }
        }
    }
}
