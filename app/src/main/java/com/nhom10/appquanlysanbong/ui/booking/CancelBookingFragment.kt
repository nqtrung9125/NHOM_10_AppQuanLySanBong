
package com.nhom10.appquanlysanbong.ui.booking

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.databinding.FragmentCancelBookingBinding
import com.nhom10.appquanlysanbong.ui.history.BookingHistoryFragment

class CancelBookingFragment : Fragment(R.layout.fragment_cancel_booking) {

    private var _binding: FragmentCancelBookingBinding? = null
    private val binding get() = _binding!!
    private val cancelInfo get() = DemoData.cancellationInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCancelBookingBinding.bind(view)

        setupAppbar()
        loadCancellationInfo()
        setupEvents()
    }

    private fun setupAppbar() {
        binding.appbar.tvAppbarTitle.text = "Hủy booking & hoàn cọc"

        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadCancellationInfo() {
        binding.tvCancelCourt.text = cancelInfo.courtName
        binding.tvCancelDateTime.text = cancelInfo.dateTime
        binding.tvCancelCode.text = cancelInfo.bookingCode
        binding.tvRefundAmount.text = cancelInfo.depositAmount
        binding.tvRefundReadonly.text = cancelInfo.refundAmount
        binding.tvRefundPolicy.text = cancelInfo.refundPolicy
    }

    private fun setupEvents() {
        binding.btnConfirmCancel.setOnClickListener {
            showConfirmDialog()
        }
    }

    private fun showConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận hủy booking?")
            .setMessage(
                "Bạn sẽ gửi yêu cầu hủy booking ${cancelInfo.bookingCode} " +
                        "và hoàn ${cancelInfo.refundAmount} về ${cancelInfo.refundMethod}."
            )
            .setNegativeButton("Quay lại", null)
            .setPositiveButton("Xác nhận") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "Đã gửi yêu cầu hủy và hoàn cọc (demo)",
                    Toast.LENGTH_LONG
                ).show()

                (requireActivity() as Navigator)
                    .navigateAsRoot(BookingHistoryFragment())
            }
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
