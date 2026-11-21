package com.nhom10.appquanlysanbong.ui.pass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.PassData
import com.nhom10.appquanlysanbong.data.PassItem
import com.nhom10.appquanlysanbong.databinding.FragmentPassDetailBinding
import com.nhom10.appquanlysanbong.ui.common.vnd
import com.nhom10.appquanlysanbong.ui.pass.PassExpiredFragment

/** KH-22 Chi tiết tin pass. */
class PassDetailFragment : Fragment(R.layout.fragment_pass_detail) {

    private var _binding: FragmentPassDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var passItem: PassItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments?.getString(ARG_CODE) ?: PassData.samplePasses.first().passCode
        passItem = PassData.samplePasses.firstOrNull { it.passCode == code } ?: PassData.samplePasses.first()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPassDetailBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Chi tiết tin pass"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.tvCourtName.text = passItem.courtName
        binding.tvTimerBadge.text = passItem.timerBadge
        binding.tvDatetime.text = "${passItem.date} · ${passItem.time}"
        binding.tvPassCode.text = passItem.passCode
        binding.tvTotal.text = passItem.totalVnd.vnd()
        binding.tvDeposit.text = passItem.depositVnd.vnd()
        binding.tvPayNow.text = passItem.depositVnd.vnd()
        binding.tvRemain.text = passItem.remainVnd.vnd()

        binding.tvCourtType.text = passItem.courtType
        binding.tvDuration.text = passItem.duration
        binding.tvPoster.text = passItem.poster
        binding.tvDeadline.text = passItem.deadline

        binding.btnReceiveCourt.setOnClickListener {
            if (passItem.courtName.contains("B1") || passItem.passCode.contains("17")) {
                navigator.navigateTo(PassExpiredFragment.newInstance(passItem.passCode))
            } else {
                navigator.navigateTo(ReceiveCourtConfirmFragment.newInstance(passItem.passCode))
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_CODE = "pass_code"
        fun newInstance(code: String) = PassDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_CODE, code) }
        }
    }
}
