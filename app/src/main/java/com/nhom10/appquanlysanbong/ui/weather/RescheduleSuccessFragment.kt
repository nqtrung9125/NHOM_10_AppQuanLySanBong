package com.nhom10.appquanlysanbong.ui.weather

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentRescheduleSuccessBinding
import com.nhom10.appquanlysanbong.ui.courts.CourtListFragment
import com.nhom10.appquanlysanbong.ui.history.BookingHistoryFragment

/** KH-19 Dời lịch thành công: xác nhận thông tin lịch mới và mã QR. */
class RescheduleSuccessFragment : Fragment(R.layout.fragment_reschedule_success) {

    private var _binding: FragmentRescheduleSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRescheduleSuccessBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Dời lịch thành công"
        binding.appbar.btnBack.setOnClickListener {
            navigator.navigateAsRoot(CourtListFragment())
        }

        binding.btnViewBooking.setOnClickListener {
            navigator.navigateAsRoot(BookingHistoryFragment())
        }
        binding.btnHome.setOnClickListener {
            navigator.navigateAsRoot(CourtListFragment())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
