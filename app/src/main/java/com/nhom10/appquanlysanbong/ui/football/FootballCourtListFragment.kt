package com.nhom10.appquanlysanbong.ui.football

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.Sport
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.data.courts.displayName
import com.nhom10.appquanlysanbong.databinding.FragmentFootballCourtListBinding
import com.nhom10.appquanlysanbong.databinding.ItemFootballCourtBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab

/** KH-32 Danh sách sân bóng đá. */
class FootballCourtListFragment : Fragment(R.layout.fragment_football_court_list) {

    private var _binding: FragmentFootballCourtListBinding? = null
    private val binding get() = _binding!!

    private var selectedFilter = 0 // 0: Tất cả, 1: Sân 5, 2: Sân 7, 3: Sân 11
    private var chips = emptyList<TextView>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFootballCourtListBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Sân bóng đá"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        BottomNav.setup(binding.bottomNav, NavTab.HOME, navigator)

        chips = listOf(binding.chipAll, binding.chip5, binding.chip7, binding.chip11)
        chips.forEachIndexed { index, chip ->
            chip.setOnClickListener {
                selectedFilter = index
                renderChips()
                renderCourts(navigator)
            }
        }

        renderChips()
        renderCourts(navigator)
    }

    private fun renderChips() {
        val ctx = requireContext()
        chips.forEachIndexed { index, chip ->
            val on = index == selectedFilter
            chip.backgroundTintList = if (on) ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.brand)) else null
            chip.setTextColor(ContextCompat.getColor(ctx, if (on) android.R.color.white else R.color.text_primary))
            if (on) {
                chip.setTypeface(null, Typeface.BOLD)
            } else {
                chip.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    private fun renderCourts(navigator: Navigator) {
        binding.llFootballCourts.removeAllViews()

        val size = listOf(null, 5, 7, 11)[selectedFilter]
        val filtered = CourtCatalog(requireContext()).all().filter {
            it.sport == Sport.FOOTBALL && (size == null || it.maxPlayers == size)
        }

        filtered.forEach { court ->
            val cardBinding = ItemFootballCourtBinding.inflate(layoutInflater, binding.llFootballCourts, false)
            cardBinding.tvCourtName.text = court.displayName
            cardBinding.tvCourtStatus.text = if (court.available) "Hoạt động" else "Tạm ngưng"
            cardBinding.tvCourtSub.text = court.description
            cardBinding.tvCourtPrice.text = court.priceFrom

            cardBinding.root.setOnClickListener {
                navigator.navigateTo(FootballCourtDetailFragment.newInstance(court.id))
            }

            binding.llFootballCourts.addView(cardBinding.root)
        }
    }

    override fun onDestroyView() {
        chips = emptyList()
        _binding = null
        super.onDestroyView()
    }
}
