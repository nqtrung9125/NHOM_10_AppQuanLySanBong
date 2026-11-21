package com.nhom10.appquanlysanbong.ui.matchmaking

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentMatchmakingRequestBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab

/** KH-36 Nhờ nhân viên cáp kèo. */
class MatchmakingRequestFragment : Fragment(R.layout.fragment_matchmaking_request) {

    private var _binding: FragmentMatchmakingRequestBinding? = null
    private val binding get() = _binding!!

    private var selectedTab = 0 // 0: Bóng đá, 1: Cầu lông, 2: Pickleball
    private lateinit var tabs: List<TextView>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchmakingRequestBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Nhờ nhân viên cáp kèo"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        BottomNav.setup(binding.bottomNav, NavTab.MATCH, navigator)

        tabs = listOf(binding.tabFootball, binding.tabBadminton, binding.tabPickleball)
        tabs.forEachIndexed { index, tab ->
            tab.setOnClickListener {
                selectedTab = index
                renderTabs()
            }
        }
        renderTabs()

        binding.btnViewSentProposals.setOnClickListener {
            navigator.navigateTo(MatchmakingMyRequestsFragment())
        }

        binding.btnSendRequest.setOnClickListener {
            Toast.makeText(requireContext(), "Đã gửi yêu cầu tìm đối thành công", Toast.LENGTH_SHORT).show()
            navigator.navigateTo(MatchmakingMyRequestsFragment())
        }
    }

    private fun renderTabs() {
        val ctx = requireContext()
        val textPrimary = ContextCompat.getColor(ctx, R.color.text_primary)
        val white = ContextCompat.getColor(ctx, android.R.color.white)

        tabs.forEachIndexed { index, tab ->
            val on = index == selectedTab
            tab.background = if (on) ContextCompat.getDrawable(ctx, R.drawable.bg_button_primary) else null
            tab.setTextColor(if (on) white else textPrimary)
            tab.typeface = if (on) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
