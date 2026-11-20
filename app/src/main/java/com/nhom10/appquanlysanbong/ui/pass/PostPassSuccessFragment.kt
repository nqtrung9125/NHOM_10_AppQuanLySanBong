package com.nhom10.appquanlysanbong.ui.pass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.BookingStatus
import com.nhom10.appquanlysanbong.databinding.FragmentPostPassSuccessBinding
import com.nhom10.appquanlysanbong.ui.booking.BookingDetailFragment
import com.nhom10.appquanlysanbong.ui.courts.CourtListFragment

/** KH-31 Đã đăng pass sân thành công (đang chờ người nhận). */
class PostPassSuccessFragment : Fragment(R.layout.fragment_post_pass_success) {

    private var _binding: FragmentPostPassSuccessBinding? = null
    private val binding get() = _binding!!

    private var court: String = "Sân A3 · Cầu lông"
    private var date: String = "Thứ Sáu, 16/10/2026"
    private var time: String = "18:00 – 20:00"
    private var bookingCode: String = "DS2026101600031"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            court = it.getString(ARG_COURT, court)
            date = it.getString(ARG_DATE, date)
            time = it.getString(ARG_TIME, time)
            bookingCode = it.getString(ARG_CODE, bookingCode)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostPassSuccessBinding.bind(view)

        val navigator = requireActivity() as Navigator

        binding.tvCourt.text = court
        binding.tvDatetime.text = "$date · $time"
        binding.tvPassCode.text = "PS2026101600004"

        binding.btnViewBooking.setOnClickListener {
            navigator.navigateTo(
                BookingDetailFragment.newInstance(
                    status = BookingStatus.PASSING,
                    court = court,
                    date = date,
                    time = time,
                    total = 400_000,
                    deposit = 120_000,
                    code = bookingCode
                )
            )
        }

        binding.btnViewPasses.setOnClickListener {
            navigator.navigateTo(PassListFragment())
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
        private const val ARG_CODE = "code"

        fun newInstance(court: String, date: String, time: String, bookingCode: String) =
            PostPassSuccessFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURT, court)
                    putString(ARG_DATE, date)
                    putString(ARG_TIME, time)
                    putString(ARG_CODE, bookingCode)
                }
            }
    }
}
