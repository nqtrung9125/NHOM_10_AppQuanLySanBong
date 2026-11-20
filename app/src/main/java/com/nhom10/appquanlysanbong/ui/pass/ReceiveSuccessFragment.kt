package com.nhom10.appquanlysanbong.ui.pass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.BookingStatus
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.data.PassData
import com.nhom10.appquanlysanbong.data.PassItem
import com.nhom10.appquanlysanbong.databinding.FragmentReceiveSuccessBinding
import com.nhom10.appquanlysanbong.ui.booking.BookingDetailFragment
import com.nhom10.appquanlysanbong.ui.common.vnd
import com.nhom10.appquanlysanbong.ui.courts.CourtListFragment

/** KH-25 Nhận sân chuyển nhượng thành công. */
class ReceiveSuccessFragment : Fragment(R.layout.fragment_receive_success) {

    private var _binding: FragmentReceiveSuccessBinding? = null
    private val binding get() = _binding!!

    private lateinit var passItem: PassItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments?.getString(ARG_CODE) ?: PassData.samplePasses.first().passCode
        passItem = PassData.samplePasses.firstOrNull { it.passCode == code } ?: PassData.samplePasses.first()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReceiveSuccessBinding.bind(view)

        val navigator = requireActivity() as Navigator

        binding.qrCode.setMatrix(PassData.samplePasses.first().let { DemoData.QR_SAMPLE })
        binding.tvBookingCode.text = "DS2026101600031"
        binding.tvCourt.text = passItem.courtName
        binding.tvDate.text = passItem.date
        binding.tvTime.text = passItem.time
        binding.tvTotal.text = passItem.totalVnd.vnd()
        binding.tvDeposit.text = passItem.depositVnd.vnd()
        binding.tvRemain.text = passItem.remainVnd.vnd()

        binding.btnViewBooking.setOnClickListener {
            navigator.navigateTo(
                BookingDetailFragment.newInstance(
                    status = BookingStatus.DEPOSIT_PAID,
                    court = passItem.courtName,
                    date = passItem.date,
                    time = passItem.time,
                    total = passItem.totalVnd,
                    deposit = passItem.depositVnd,
                    code = "DS2026101600031"
                )
            )
        }

        binding.btnHome.setOnClickListener {
            navigator.navigateAsRoot(CourtListFragment())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_CODE = "pass_code"
        fun newInstance(code: String) = ReceiveSuccessFragment().apply {
            arguments = Bundle().apply { putString(ARG_CODE, code) }
        }
    }
}
