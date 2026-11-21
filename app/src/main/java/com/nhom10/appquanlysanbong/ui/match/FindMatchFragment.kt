
package com.nhom10.appquanlysanbong.ui.match

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.data.MatchItem
import com.nhom10.appquanlysanbong.databinding.FragmentFindMatchBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab
import com.nhom10.appquanlysanbong.ui.matchmaking.CustomerMatchmakingFragment

/** KH-11: Tìm trận và tìm bạn chơi. */
class FindMatchFragment : Fragment(R.layout.fragment_find_match) {

    private var _binding: FragmentFindMatchBinding? = null
    private val binding get() = _binding!!

    private val matches = DemoData.matchItems

    private var selectedSport: String? = null
    private var todayOnly = false
    private var selectedMode = "Tìm bạn chơi"
    private var suggestionsOnly = false
    private val suggestedLevels = DemoData.LEVELS.drop(DemoData.DEFAULT_LEVEL).take(2)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFindMatchBinding.bind(view)

        BottomNav.setup(
            binding.bottomNav,
            NavTab.MATCH,
            requireActivity() as Navigator
        )

        binding.tvCreateMatch.setOnClickListener {
            (requireActivity() as Navigator).navigateTo(CreateMatchFragment())
        }

        binding.btnStaffMatch.setOnClickListener {
            (requireActivity() as Navigator).navigateTo(CustomerMatchmakingFragment())
        }

        binding.tabFindOpponent.setOnClickListener {
            selectedMode = "Bắt đối"
            updateModeStyles()
            renderMatches()
        }

        binding.tabFindFriends.setOnClickListener {
            selectedMode = "Tìm bạn chơi"
            updateModeStyles()
            renderMatches()
        }

        binding.chipNear.setOnClickListener {
            todayOnly = false
            updateFilterStyles()
            renderMatches()
        }

        binding.chipToday.setOnClickListener {
            todayOnly = true
            updateFilterStyles()
            renderMatches()
        }

        binding.chipBadminton.setOnClickListener {
            selectedSport =
                if (selectedSport == "Cầu lông") null else "Cầu lông"

            updateFilterStyles()
            renderMatches()
        }

        binding.chipPickleball.setOnClickListener {
            selectedSport =
                if (selectedSport == "Pickleball") null else "Pickleball"

            updateFilterStyles()
            renderMatches()
        }

        binding.btnMatchSuggestions.setOnClickListener {
            suggestionsOnly = !suggestionsOnly
            renderMatches()
        }

        updateModeStyles()
        updateFilterStyles()
        renderMatches()
    }

    private fun updateModeStyles() {
        val ctx = requireContext()
        val opponentSelected = selectedMode == "Bắt đối"

        if (opponentSelected) {
            binding.tabFindOpponent.setBackgroundResource(R.drawable.bg_card)
        } else {
            binding.tabFindOpponent.setBackgroundColor(Color.TRANSPARENT)
        }

        if (!opponentSelected) {
            binding.tabFindFriends.setBackgroundResource(R.drawable.bg_card)
        } else {
            binding.tabFindFriends.setBackgroundColor(Color.TRANSPARENT)
        }

        binding.tabFindOpponent.setTextColor(
            ContextCompat.getColor(
                ctx,
                if (opponentSelected) R.color.text_primary
                else R.color.text_muted
            )
        )

        binding.tabFindFriends.setTextColor(
            ContextCompat.getColor(
                ctx,
                if (!opponentSelected) R.color.text_primary
                else R.color.text_muted
            )
        )

        binding.tabFindOpponent.setTypeface(
            null,
            if (opponentSelected) Typeface.BOLD else Typeface.NORMAL
        )

        binding.tabFindFriends.setTypeface(
            null,
            if (!opponentSelected) Typeface.BOLD else Typeface.NORMAL
        )
    }

    private fun updateFilterStyles() {
        styleChip(binding.chipNear, !todayOnly)
        styleChip(binding.chipToday, todayOnly)
        styleChip(binding.chipBadminton, selectedSport == "Cầu lông")
        styleChip(binding.chipPickleball, selectedSport == "Pickleball")
    }

    private fun styleChip(chip: TextView, selected: Boolean) {
        val ctx = requireContext()

        chip.setTextColor(
            ContextCompat.getColor(
                ctx,
                if (selected) android.R.color.white
                else R.color.text_primary
            )
        )

        chip.background = ContextCompat.getDrawable(
            ctx,
            if (selected) R.drawable.bg_button_primary
            else R.drawable.bg_pill
        )
    }

    private fun renderMatches() {
        binding.btnMatchSuggestions.setText(if (suggestionsOnly) R.string.match_suggestions_reset else R.string.match_suggestions_show)
        binding.btnMatchSuggestions.isSelected = suggestionsOnly
        val filtered = matches.filter { match ->
            (selectedSport == null || match.sport == selectedSport) &&
                (!todayOnly || match.schedule.startsWith("Hôm nay")) &&
                (!suggestionsOnly || match.level in suggestedLevels)
        }

        binding.llMatches.removeAllViews()

        if (filtered.isEmpty()) {
            binding.llMatches.addView(TextView(requireContext()).apply {
                text = "Chưa có trận phù hợp với bộ lọc."
                textSize = 13f
                gravity = Gravity.CENTER
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_muted
                    )
                )
                setPadding(0, dp(24), 0, dp(24))
            })
            return
        }

        filtered.forEach { match ->
            binding.llMatches.addView(createMatchCard(match))
        }
    }

    private fun createMatchCard(match: MatchItem): View {
        val ctx = requireContext()

        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = ContextCompat.getDrawable(ctx, R.drawable.bg_card)
            elevation = dp(1).toFloat()
        }

        card.addView(TextView(ctx).apply {
            text = match.court
            textSize = 15f
            setTextColor(
                ContextCompat.getColor(ctx, R.color.text_primary)
            )
            typeface = Typeface.DEFAULT_BOLD
        })

        card.addView(TextView(ctx).apply {
            text = "Trình độ: ${match.level}"
            textSize = 12f
            setTextColor(ContextCompat.getColor(ctx, R.color.brand))
            setPadding(0, dp(4), 0, dp(3))
        })

        card.addView(TextView(ctx).apply {
            text = match.schedule
            textSize = 12f
            setTextColor(
                ContextCompat.getColor(ctx, R.color.text_muted)
            )
        })

        val bottomRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(10), 0, 0)
        }

        bottomRow.addView(TextView(ctx).apply {
            text = "${match.players} · ${match.missing}\n${match.price}"
            textSize = 12f
            setTextColor(
                ContextCompat.getColor(ctx, R.color.text_primary)
            )
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        })

        bottomRow.addView(TextView(ctx).apply {
            text = if (selectedMode == "Bắt đối") {
                "Gửi lời mời"
            } else {
                "Tham gia"
            }

            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(
                ContextCompat.getColor(ctx, android.R.color.white)
            )
            setPadding(dp(14), dp(9), dp(14), dp(9))
            background = ContextCompat.getDrawable(
                ctx,
                R.drawable.bg_button_primary
            )

            setOnClickListener {
                val message = if (selectedMode == "Bắt đối") {
                    "Đã gửi lời mời bắt đối (demo)"
                } else {
                    "Đã gửi yêu cầu tham gia trận (demo)"
                }

                Toast.makeText(
                    ctx,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        card.addView(bottomRow)

        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(10)
        }

        return card
    }

    private fun comingSoon() {
        Toast.makeText(
            requireContext(),
            R.string.msg_coming_soon,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
