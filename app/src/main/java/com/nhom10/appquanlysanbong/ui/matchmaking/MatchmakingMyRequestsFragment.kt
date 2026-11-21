package com.nhom10.appquanlysanbong.ui.matchmaking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentMatchmakingMyRequestsBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab
import com.nhom10.appquanlysanbong.ui.match.FindMatchFragment

/** KH-40 Yêu cầu cáp kèo của tôi. */
class MatchmakingMyRequestsFragment : Fragment(R.layout.fragment_matchmaking_my_requests) {

    private var _binding: FragmentMatchmakingMyRequestsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchmakingMyRequestsBinding.bind(view)

        val navigator = requireActivity() as Navigator

        BottomNav.setup(binding.bottomNav, NavTab.MATCH, navigator)

        binding.btnViewReceivedProposal.setOnClickListener {
            navigator.navigateTo(MatchmakingProposalFragment())
        }

        binding.btnFindMatch.setOnClickListener {
            navigator.navigateAsRoot(FindMatchFragment())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
