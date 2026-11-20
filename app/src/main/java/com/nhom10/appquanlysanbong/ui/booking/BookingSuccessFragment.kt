package com.nhom10.appquanlysanbong.ui.booking

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.data.BookingStatus
import com.nhom10.appquanlysanbong.databinding.FragmentBookingSuccessBinding
import com.nhom10.appquanlysanbong.ui.common.toastComingSoon
import com.nhom10.appquanlysanbong.ui.common.vnd
import com.nhom10.appquanlysanbong.ui.courts.CourtListFragment
import com.nhom10.appquanlysanbong.ui.booking.CancelBookingFragment

/** KH-07 Thanh toán cọc thành công: mã QR và thông tin booking. */
class BookingSuccessFragment : Fragment(R.layout.fragment_booking_success) {

    private var _binding: FragmentBookingSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookingSuccessBinding.bind(view)
        val navigator = requireActivity() as Navigator
        navigator.setStatusBarLight(true) // màn này nền sáng

        val args = requireArguments()
        val total = args.getInt(ARG_TOTAL)
        val deposit = args.getInt(ARG_DEPOSIT)

        binding.qrCode.setMatrix(DemoData.QR_SAMPLE)
        binding.tvBookingCode.text = args.getString(ARG_CODE)

        binding.rowCourt.tvRowLabel.setText(R.string.summary_court)
        binding.rowCourt.tvRowValue.text = args.getString(ARG_COURT)
        binding.rowDate.tvRowLabel.setText(R.string.summary_date)
        binding.rowDate.tvRowValue.text = args.getString(ARG_DATE)
        binding.rowTime.tvRowLabel.setText(R.string.row_hour)
        binding.rowTime.tvRowValue.text = args.getString(ARG_TIME)
        binding.rowTotal.tvRowLabel.setText(R.string.summary_total)
        binding.rowTotal.tvRowValue.text = total.vnd()

        binding.rowDeposit.tvRowLabel.text =
            getString(R.string.row_deposit_paid, DemoData.DEPOSIT_PERCENT)
        binding.rowDeposit.tvRowValue.text = deposit.vnd()
        binding.rowDeposit.tvRowValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand))
        binding.rowDeposit.tvRowValue.setTypeface(Typeface.DEFAULT_BOLD)

        binding.rowRemain.tvRowLabel.setText(R.string.summary_remain)
        binding.rowRemain.tvRowValue.text = (total - deposit).vnd()
        binding.rowRemain.tvRowValue.setTypeface(Typeface.DEFAULT_BOLD)

        binding.note.tvNoteText.setText(R.string.success_note)

        binding.btnViewBooking.setOnClickListener {
            navigator.navigateTo(
                BookingDetailFragment.newInstance(
                    status = BookingStatus.DEPOSIT_PAID,
                    court = args.getString(ARG_COURT),
                    date = args.getString(ARG_DATE),
                    time = args.getString(ARG_TIME),
                    total = total,
                    deposit = deposit,
                    code = args.getString(ARG_CODE)
                )
            )
        }
        binding.tvHome.setOnClickListener { navigator.navigateAsRoot(CourtListFragment()) }
    }

    override fun onDestroyView() {
        (activity as? Navigator)?.setStatusBarLight(false)
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
        ) = BookingSuccessFragment().apply {
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