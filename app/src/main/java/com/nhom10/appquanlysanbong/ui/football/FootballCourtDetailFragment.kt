package com.nhom10.appquanlysanbong.ui.football

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.data.courts.displayName
import com.nhom10.appquanlysanbong.databinding.FragmentFootballCourtDetailBinding
import com.nhom10.appquanlysanbong.databinding.ItemRowBinding
import com.nhom10.appquanlysanbong.ui.booking.BookingFragment
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab

/** KH-43. Bảng giá hiện tại và lối vào luồng đặt sân dùng chung. */
class FootballCourtDetailFragment : Fragment(R.layout.fragment_football_court_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val screen = FragmentFootballCourtDetailBinding.bind(view)
        val navigator = requireActivity() as Navigator
        screen.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        BottomNav.setup(screen.bottomNav, NavTab.HOME, navigator)
        val court = CourtCatalog(requireContext()).find(arguments?.getString(COURT_ID))
        if (court == null) {
            screen.appbar.tvAppbarTitle.text = "Sân bóng đá"
            screen.tvSubtitle.text = "Không tìm thấy sân."
            screen.btnPickTime.isVisible = false
            return
        }
        screen.appbar.tvAppbarTitle.text = court.displayName
        screen.tvSubtitle.text = if (court.available) "Sân phù hợp đội ${court.maxPlayers} người" else "Sân đang tạm ngưng"
        screen.tvAmenities.text = court.description
        screen.tvPricingTitle.text = "Bảng giá sân ${court.maxPlayers} người"
        court.prices.forEach { price ->
            val row = ItemRowBinding.inflate(layoutInflater, screen.llPriceRows, false)
            row.tvRowLabel.text = price.label
            row.tvRowValue.text = price.price
            screen.llPriceRows.addView(row.root)
        }
        screen.btnPickTime.isVisible = court.available
        screen.btnPickTime.setOnClickListener {
            navigator.navigateTo(BookingFragment.newInstance(court.id))
        }
    }

    companion object {
        private const val COURT_ID = "court_id"

        fun newInstance(courtId: String) = FootballCourtDetailFragment().apply {
            arguments = Bundle().apply { putString(COURT_ID, courtId) }
        }
    }
}