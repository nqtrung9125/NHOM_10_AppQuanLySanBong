package com.nhom10.appquanlysanbong.ui.matchmaking

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentMatchmakingTeamPaymentBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab

/** KH-41 Thanh toán cọc đội bạn. */
class MatchmakingTeamPaymentFragment : Fragment(R.layout.fragment_matchmaking_team_payment) {

    private var _binding: FragmentMatchmakingTeamPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchmakingTeamPaymentBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Thanh toán cọc đội bạn"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        BottomNav.setup(binding.bottomNav, NavTab.MATCH, navigator)

        binding.btnPayTeamDeposit.setOnClickListener {
            Toast.makeText(requireContext(), "Đã thanh toán cọc 75.000đ thành công", Toast.LENGTH_SHORT).show()
            navigator.navigateTo(MatchmakingWaitingPaymentFragment())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
