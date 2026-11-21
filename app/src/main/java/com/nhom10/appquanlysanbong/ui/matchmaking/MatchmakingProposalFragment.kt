package com.nhom10.appquanlysanbong.ui.matchmaking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentMatchmakingProposalBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab

/** KH-37 Đề xuất cáp kèo từ nhân viên. */
class MatchmakingProposalFragment : Fragment(R.layout.fragment_matchmaking_proposal) {

    private var _binding: FragmentMatchmakingProposalBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchmakingProposalBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Đề xuất cáp kèo"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        BottomNav.setup(binding.bottomNav, NavTab.MATCH, navigator)

        binding.btnReject.setOnClickListener {
            navigator.navigateTo(MatchmakingRejectedFragment())
        }

        binding.btnAccept.setOnClickListener {
            navigator.navigateTo(MatchmakingTeamPaymentFragment())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
