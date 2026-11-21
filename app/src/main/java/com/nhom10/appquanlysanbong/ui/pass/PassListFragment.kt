package com.nhom10.appquanlysanbong.ui.pass

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.PassData
import com.nhom10.appquanlysanbong.databinding.FragmentPassListBinding
import com.nhom10.appquanlysanbong.databinding.ItemPassCardBinding
import com.nhom10.appquanlysanbong.ui.common.vnd

/** KH-21 Danh sách sân đang được pass. */
class PassListFragment : Fragment(R.layout.fragment_pass_list) {

    private var _binding: FragmentPassListBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPassListBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Sân đang được pass"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnPostPass.setOnClickListener {
            navigator.navigateTo(SelectBookingPassFragment())
        }

        renderPassList(navigator)
    }

    private fun renderPassList(navigator: Navigator) {
        binding.llPassList.removeAllViews()
        PassData.samplePasses.forEach { pass ->
            val cardBinding = ItemPassCardBinding.inflate(layoutInflater, binding.llPassList, false)
            cardBinding.tvCourtName.text = pass.courtName
            cardBinding.tvTimerBadge.text = pass.timerBadge
            cardBinding.tvDatetime.text = "${pass.date} · ${pass.time}"
            cardBinding.tvTotalPrice.text = pass.totalVnd.vnd()
            cardBinding.tvDepositPrice.text = pass.depositVnd.vnd()
            cardBinding.tvRemainPrice.text = pass.remainVnd.vnd()

            cardBinding.btnViewDetail.setOnClickListener {
                navigator.navigateTo(PassDetailFragment.newInstance(pass.passCode))
            }
            binding.llPassList.addView(cardBinding.root)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
