package com.nhom10.appquanlysanbong.ui.courts

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.Court
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.data.Sport
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.data.courts.displayName
import com.nhom10.appquanlysanbong.data.session.SessionStore
import com.nhom10.appquanlysanbong.databinding.FragmentCourtListBinding
import com.nhom10.appquanlysanbong.databinding.ItemBannerBinding
import com.nhom10.appquanlysanbong.databinding.ItemCourtBinding
import java.util.Locale
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab
import com.nhom10.appquanlysanbong.ui.notifications.NotificationsFragment
import com.nhom10.appquanlysanbong.ui.pass.PassMarketFragment
import com.nhom10.appquanlysanbong.ui.match.FindMatchFragment
import com.nhom10.appquanlysanbong.ui.football.FootballCourtListFragment
import com.nhom10.appquanlysanbong.ui.weather.WeatherFragment
/** KH-04 Danh sách sân (trang chủ): lọc theo môn, tìm theo tên, banner, bottom nav. */
class CourtListFragment : Fragment(R.layout.fragment_court_list) {

    private var _binding: FragmentCourtListBinding? = null
    private val binding get() = _binding!!

    private var selected: Sport? = null // null = Tất cả
    private var chips = emptyMap<Sport?, TextView>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCourtListBinding.bind(view)

        binding.tvGreeting.text = getString(R.string.home_greeting, SessionStore(requireContext()).current.name.substringAfterLast(' '))
        binding.tvWeather.text = DemoData.WEATHER_SUMMARY
        binding.weatherChip.setOnClickListener {
            (requireActivity() as Navigator).navigateTo(WeatherFragment())
        }
        binding.cardFootball.setOnClickListener {
            (requireActivity() as Navigator).navigateTo(FootballCourtListFragment())
        }

        chips = mapOf<Sport?, TextView>(
            null to binding.chipAll,
            Sport.FOOTBALL to binding.chipFootball,
            Sport.BADMINTON to binding.chipBadminton,
            Sport.PICKLEBALL to binding.chipPickleball,
            Sport.VOLLEYBALL to binding.chipVolleyball
        )
        chips.forEach { (sport, chip) ->
            chip.setOnClickListener {
                selected = sport
                render()
            }
        }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) = render()
        })

        bindBanner(
            binding.bannerMatch,
            getString(R.string.banner_match_title),
            getString(R.string.banner_match_sub, DemoData.MATCHES_WAITING),
            getString(R.string.banner_match_cta)
        )
        bindBanner(
            binding.bannerPass,
            getString(R.string.banner_pass_title),
            getString(R.string.banner_pass_sub),
            getString(R.string.banner_pass_cta)
        )


        BottomNav.setup(binding.bottomNav, NavTab.HOME, requireActivity() as Navigator)

        render()
    }

    private fun bindBanner(banner: ItemBannerBinding, title: String, sub: String, cta: String) {
        banner.tvBannerTitle.text = title
        banner.tvBannerSub.text = sub
        banner.tvBannerCta.text = cta
        banner.root.setOnClickListener {
            if (title.contains("pass", ignoreCase = true)) {
                (requireActivity() as Navigator).navigateTo(PassMarketFragment())
            } else {
                (requireActivity() as Navigator).navigateAsRoot(FindMatchFragment())
            }
        }
    }

    private fun render() {
        chips.forEach { (sport, chip) -> styleFilterChip(chip, sport == selected) }

        val q = binding.edtSearch.text.toString().trim().lowercase(Locale.getDefault())
        val courts = CourtCatalog(requireContext()).all().filter { c ->
            (selected == c.sport || (selected == null && (c.sport != Sport.FOOTBALL || q.isNotEmpty()))) &&
                (q.isEmpty() || c.displayName.lowercase().contains(q) || c.id.lowercase().contains(q))
        }
        val showFootball = (selected == null || selected == Sport.FOOTBALL) &&
                (q.isEmpty() || getString(R.string.football_card_title).lowercase().contains(q))

        binding.cardFootball.visibility = if (showFootball) View.VISIBLE else View.GONE
        binding.llCourts.removeAllViews()
        courts.forEach { binding.llCourts.addView(createCourtCard(it)) }
        binding.tvEmpty.visibility = if (courts.isEmpty() && !showFootball) View.VISIBLE else View.GONE
    }

    private fun styleFilterChip(chip: TextView, on: Boolean) {
        val ctx = requireContext()
        chip.backgroundTintList =
            if (on) ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.brand)) else null
        chip.setTextColor(
            ContextCompat.getColor(ctx, if (on) android.R.color.white else R.color.text_primary)
        )
    }

    private fun createCourtCard(court: Court): View {
        val ctx = requireContext()
        val card = ItemCourtBinding.inflate(layoutInflater, binding.llCourts, false)
        card.tvCourtName.text = court.displayName
        card.tvCourtDesc.text = court.description
        card.tvCourtPrice.text = court.priceFrom

        card.tvCourtStatus.setText(if (court.available) R.string.status_available else R.string.status_in_use)
        card.tvCourtStatus.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(ctx, if (court.available) R.color.brand_tint else R.color.warning_bg)
        )
        card.tvCourtStatus.setTextColor(
            ContextCompat.getColor(ctx, if (court.available) R.color.brand else R.color.warning_text)
        )

        card.root.setOnClickListener {
            (requireActivity() as Navigator).navigateTo(CourtDetailFragment.newInstance(court.id))
        }
        return card.root
    }

    override fun onDestroyView() {
        chips = emptyMap()
        _binding = null
        super.onDestroyView()
    }
}
