package com.nhom10.appquanlysanbong.ui.matchmaking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentMatchmakingRejectedBinding

/** KH-39 Đã từ chối đề xuất cáp kèo. */
class MatchmakingRejectedFragment : Fragment(R.layout.fragment_matchmaking_rejected) {

    private var _binding: FragmentMatchmakingRejectedBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchmakingRejectedBinding.bind(view)

        val navigator = requireActivity() as Navigator

        binding.btnMyRequests.setOnClickListener {
            navigator.navigateTo(MatchmakingMyRequestsFragment())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
