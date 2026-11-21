package com.nhom10.appquanlysanbong.ui.weather

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentRescheduleBinding
import com.nhom10.appquanlysanbong.ui.booking.CancelBookingFragment

/** KH-18 Đề xuất dời lịch: hiển thị cảnh báo thời tiết và các khung giờ thay thế. */
class RescheduleFragment : Fragment(R.layout.fragment_reschedule) {

    private var _binding: FragmentRescheduleBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRescheduleBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Đề xuất dời lịch"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.cardOption1.setOnClickListener {
            binding.radio1.isChecked = true
            binding.radio2.isChecked = false
            binding.radio3.isChecked = false
        }
        binding.cardOption2.setOnClickListener {
            binding.radio1.isChecked = false
            binding.radio2.isChecked = true
            binding.radio3.isChecked = false
        }
        binding.cardOption3.setOnClickListener {
            binding.radio1.isChecked = false
            binding.radio2.isChecked = false
            binding.radio3.isChecked = true
        }

        binding.btnConfirmReschedule.setOnClickListener {
            navigator.navigateTo(RescheduleSuccessFragment())
        }
        binding.btnKeepBooking.setOnClickListener {
            Toast.makeText(requireContext(), "Đã giữ nguyên lịch đặt", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnCancelBooking.setOnClickListener {
            navigator.navigateTo(CancelBookingFragment())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
