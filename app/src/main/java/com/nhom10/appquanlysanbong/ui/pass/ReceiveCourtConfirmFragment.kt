package com.nhom10.appquanlysanbong.ui.pass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.PassData
import com.nhom10.appquanlysanbong.data.PassItem
import com.nhom10.appquanlysanbong.databinding.FragmentReceiveCourtConfirmBinding
import com.nhom10.appquanlysanbong.ui.common.vnd

/** KH-23 Xác nhận nhận sân chuyển nhượng. */
class ReceiveCourtConfirmFragment : Fragment(R.layout.fragment_receive_court_confirm) {

    private var _binding: FragmentReceiveCourtConfirmBinding? = null
    private val binding get() = _binding!!

    private lateinit var passItem: PassItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments?.getString(ARG_CODE) ?: PassData.samplePasses.first().passCode
        passItem = PassData.samplePasses.firstOrNull { it.passCode == code } ?: PassData.samplePasses.first()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReceiveCourtConfirmBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Xác nhận nhận sân"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.tvCourt.text = passItem.courtName
        binding.tvDate.text = passItem.date
        binding.tvTime.text = "${passItem.time} (${passItem.duration})"
        binding.tvTotal.text = passItem.totalVnd.vnd()
        binding.tvDeposit.text = passItem.depositVnd.vnd()
        binding.tvRemain.text = passItem.remainVnd.vnd()
        binding.btnConfirmPay.text = "Xác nhận & thanh toán ${passItem.depositVnd.vnd()}"

        binding.tvBannerMsg.text = "Sau khi thanh toán, booking chuyển sang tên bạn và tiền cọc ${passItem.depositVnd.vnd()} được tính vào tiền sân. Bạn chỉ trả ${passItem.remainVnd.vnd()} còn lại khi đến sân."

        binding.btnConfirmPay.setOnClickListener {
            navigator.navigateTo(PassPaymentFragment.newInstance(passItem.passCode))
        }
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_CODE = "pass_code"
        fun newInstance(code: String) = ReceiveCourtConfirmFragment().apply {
            arguments = Bundle().apply { putString(ARG_CODE, code) }
        }
    }
}
