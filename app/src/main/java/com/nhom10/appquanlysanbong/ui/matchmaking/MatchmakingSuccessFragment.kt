package com.nhom10.appquanlysanbong.ui.matchmaking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentMatchmakingSuccessBinding
import com.nhom10.appquanlysanbong.ui.courts.CourtListFragment
import com.nhom10.appquanlysanbong.ui.pass.PassListFragment

/** KH-38 Đặt sân thành công qua cáp kèo. */
class MatchmakingSuccessFragment : Fragment(R.layout.fragment_matchmaking_success) {

    private var _binding: FragmentMatchmakingSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchmakingSuccessBinding.bind(view)

        val navigator = requireActivity() as Navigator

        binding.btnViewPasses.setOnClickListener {
            navigator.navigateTo(PassListFragment())
        }

        binding.btnHome.setOnClickListener {
            navigator.navigateAsRoot(CourtListFragment())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
