package com.nhom10.appquanlysanbong.ui.pass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.PassData
import com.nhom10.appquanlysanbong.data.PassItem
import com.nhom10.appquanlysanbong.databinding.FragmentPassPaymentBinding
import com.nhom10.appquanlysanbong.ui.common.tintBackground
import com.nhom10.appquanlysanbong.ui.common.vnd

/** KH-24 Thanh toán cọc nhận sân chuyển nhượng. */
class PassPaymentFragment : Fragment(R.layout.fragment_pass_payment) {

    private var _binding: FragmentPassPaymentBinding? = null
    private val binding get() = _binding!!

    private lateinit var passItem: PassItem
    private var selectedMethod = 0 // 0: wallet, 1: qr, 2: card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments?.getString(ARG_CODE) ?: PassData.samplePasses.first().passCode
        passItem = PassData.samplePasses.firstOrNull { it.passCode == code } ?: PassData.samplePasses.first()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPassPaymentBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Thanh toán cọc nhận sân"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.tvPassCode.text = passItem.passCode
        binding.tvCourt.text = passItem.courtName
        binding.tvDate.text = passItem.date
        binding.tvTime.text = "${passItem.time} (${passItem.duration})"
        binding.tvTotal.text = passItem.totalVnd.vnd()
        binding.tvDeposit.text = passItem.depositVnd.vnd()
        binding.tvRemain.text = passItem.remainVnd.vnd()
        binding.btnPayNow.text = "Thanh toán ${passItem.depositVnd.vnd()}"

        binding.methodWallet.setOnClickListener { selectMethod(0) }
        binding.methodQr.setOnClickListener { selectMethod(1) }
        binding.methodCard.setOnClickListener { selectMethod(2) }

        binding.btnPayNow.setOnClickListener {
            // Demo: nếu passCode là PS2026101800002 thì ra màn thất bại, ngược lại ra thành công
            if (passItem.passCode.contains("1800002")) {
                navigator.navigateTo(ReceiveFailureFragment.newInstance(passItem.passCode))
            } else {
                navigator.navigateTo(ReceiveSuccessFragment.newInstance(passItem.passCode))
            }
        }
    }

    private fun selectMethod(index: Int) {
        selectedMethod = index
        binding.radioWallet.isChecked = index == 0
        binding.radioQr.isChecked = index == 1
        binding.radioCard.isChecked = index == 2

        binding.methodWallet.tintBackground(if (index == 0) R.color.brand_tint else null)
        binding.methodQr.tintBackground(if (index == 1) R.color.brand_tint else null)
        binding.methodCard.tintBackground(if (index == 2) R.color.brand_tint else null)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_CODE = "pass_code"
        fun newInstance(code: String) = PassPaymentFragment().apply {
            arguments = Bundle().apply { putString(ARG_CODE, code) }
        }
    }
}
