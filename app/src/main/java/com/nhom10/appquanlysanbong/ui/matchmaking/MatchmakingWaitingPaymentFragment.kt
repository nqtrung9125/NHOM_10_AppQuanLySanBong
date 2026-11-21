package com.nhom10.appquanlysanbong.ui.matchmaking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentMatchmakingWaitingPaymentBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab

/** KH-42 Chờ đội đối thanh toán. */
class MatchmakingWaitingPaymentFragment : Fragment(R.layout.fragment_matchmaking_waiting_payment) {

    private var _binding: FragmentMatchmakingWaitingPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchmakingWaitingPaymentBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Chờ đội đối thanh toán"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        BottomNav.setup(binding.bottomNav, NavTab.MATCH, navigator)

        binding.btnMyRequests.setOnClickListener {
            navigator.navigateTo(MatchmakingMyRequestsFragment())
        }

        binding.btnCheckStatus.setOnClickListener {
            navigator.navigateTo(MatchmakingConfirmedFragment())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
