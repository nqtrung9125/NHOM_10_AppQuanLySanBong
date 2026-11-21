package com.nhom10.appquanlysanbong.ui.matchmaking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentMatchmakingConfirmedBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab
import com.nhom10.appquanlysanbong.ui.courts.CourtListFragment

/** KH-43 Trận đã được xác nhận. */
class MatchmakingConfirmedFragment : Fragment(R.layout.fragment_matchmaking_confirmed) {

    private var _binding: FragmentMatchmakingConfirmedBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchmakingConfirmedBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Trận đã được xác nhận"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        BottomNav.setup(binding.bottomNav, NavTab.MATCH, navigator)

        binding.btnHome.setOnClickListener {
            navigator.navigateAsRoot(CourtListFragment())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
