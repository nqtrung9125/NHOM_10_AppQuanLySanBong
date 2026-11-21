package com.nhom10.appquanlysanbong.ui.courts

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.data.courts.displayName
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.data.session.SessionStore
import com.nhom10.appquanlysanbong.databinding.FragmentCourtDetailBinding
import com.nhom10.appquanlysanbong.databinding.ItemRowBinding
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.ui.booking.BookingFragment

/** KH-05 Chi tiết sân. */
class CourtDetailFragment : Fragment(R.layout.fragment_court_detail) {

    private var _binding: FragmentCourtDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCourtDetailBinding.bind(view)
        val ctx = requireContext()
        val court = CourtCatalog(requireContext()).find(arguments?.getString(ARG_ID)) ?: return

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val lightText = if (court.hasLight) " · Có đèn" else ""
        binding.tvHeroTag.text = "${court.surface}$lightText · ${court.openHours}"
        binding.tvDetailName.text = court.displayName
        binding.tvDetailDesc.text = court.description

        binding.tvDetailStatus.setText(if (court.available) R.string.status_available else R.string.status_in_use)
        binding.tvDetailStatus.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(ctx, if (court.available) R.color.brand_tint else R.color.warning_bg)
        )
        binding.tvDetailStatus.setTextColor(
            ContextCompat.getColor(ctx, if (court.available) R.color.brand else R.color.warning_text)
        )

        court.prices.forEach {
            addRow(binding.llPricing, it.label, it.price, highlight = true, topMarginDp = 12)
        }

        addRow(binding.llInfo, getString(R.string.detail_type),
            "${court.sport.label} (tối đa ${court.maxPlayers} người)", false, 10)
        addRow(binding.llInfo, getString(R.string.detail_open), court.openHours, false, 10)
        addRow(binding.llInfo, getString(R.string.detail_deposit), court.deposit, false, 10)
        addRow(binding.llInfo, getString(R.string.detail_deposit_deadline), court.depositDeadline, false, 10)

        if (SessionStore(requireContext()).current.role == Role.GUEST) binding.btnBook.text = "Xem lịch trống"
        binding.btnBook.isEnabled = court.available
        if (!court.available) binding.btnBook.text = "Sân tạm ngưng"
        binding.btnBook.setOnClickListener {
            (requireActivity() as Navigator).navigateTo(BookingFragment.newInstance(court.id))
        }
    }

    private fun addRow(
        container: LinearLayout, label: String, value: String,
        highlight: Boolean, topMarginDp: Int
    ) {
        val row = ItemRowBinding.inflate(layoutInflater, container, false)
        row.tvRowLabel.text = label
        row.tvRowValue.text = value
        if (highlight) {
            row.tvRowValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand))
            row.tvRowValue.setTypeface(Typeface.DEFAULT_BOLD)
        }
        (row.root.layoutParams as LinearLayout.LayoutParams).topMargin =
            (topMarginDp * resources.displayMetrics.density).toInt()
        container.addView(row.root)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_ID = "court_id"

        fun newInstance(courtId: String) = CourtDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_ID, courtId) }
        }
    }
}
