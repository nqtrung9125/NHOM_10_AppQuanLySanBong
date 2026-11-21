package com.nhom10.appquanlysanbong.ui.match

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.databinding.FragmentCreateMatchBinding
import com.nhom10.appquanlysanbong.databinding.ItemChipBinding
import com.nhom10.appquanlysanbong.ui.common.vnd

/** KH-12 Tạo trận: chọn loại trận, số người cần thêm, trình độ và tính tiền mỗi người. */
class CreateMatchFragment : Fragment(R.layout.fragment_create_match) {

    private var _binding: FragmentCreateMatchBinding? = null
    private val binding get() = _binding!!

    private var isFindFriends = true
    private var extraPlayers = 2
    private var selectedLevelIndex = 1 // Trung bình
    private val levelOptions = listOf("Vui vẻ", "Trung bình", "Khá", "Mạnh")
    private val levelChips = mutableListOf<TextView>()

    private val courtPriceVnd = 500_000
    private val hostCount = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateMatchBinding.bind(view)

        binding.appbar.tvAppbarTitle.text = "Tạo trận"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnTypeOpponent.setOnClickListener {
            isFindFriends = false
            renderTypeToggle()
        }
        binding.btnTypeFriends.setOnClickListener {
            isFindFriends = true
            renderTypeToggle()
        }

        binding.btnMinus.setOnClickListener {
            if (extraPlayers > 1) {
                extraPlayers--
                renderPlayersAndCost()
            }
        }
        binding.btnPlus.setOnClickListener {
            if (extraPlayers < 18) {
                extraPlayers++
                renderPlayersAndCost()
            }
        }

        binding.tvProfileLevel.text = "Theo hồ sơ: ${DemoData.LEVELS.getOrElse(DemoData.DEFAULT_LEVEL) { "Trung bình" }}"
        binding.btnSuggestLevel.setOnClickListener {
            selectedLevelIndex = 1
            renderLevelChips()
        }

        buildLevelChips()
        renderTypeToggle()
        renderLevelChips()
        renderPlayersAndCost()

        binding.btnCreateMatch.setOnClickListener {
            val modeLabel = if (isFindFriends) "Tìm bạn chơi" else "Bắt đối"
            val levelLabel = levelOptions[selectedLevelIndex]
            val nearbyText = if (binding.swNearby.isChecked) " · Bán kính 5km" else ""
            Toast.makeText(
                requireContext(),
                "Đã tạo trận ($modeLabel · $levelLabel$nearbyText)",
                Toast.LENGTH_SHORT
            ).show()
            (requireActivity() as Navigator).navigateAsRoot(FindMatchFragment())
        }
    }

    private fun buildLevelChips() {
        binding.llLevels.removeAllViews()
        levelChips.clear()
        levelOptions.forEachIndexed { index, label ->
            val chip = ItemChipBinding.inflate(layoutInflater, binding.llLevels, false).root
            chip.text = label
            (chip.layoutParams as LinearLayout.LayoutParams).marginStart = if (index == 0) 0 else dp(8)
            chip.setOnClickListener {
                selectedLevelIndex = index
                renderLevelChips()
            }
            binding.llLevels.addView(chip)
            levelChips.add(chip)
        }
    }

    private fun renderTypeToggle() {
        val ctx = requireContext()
        val activeColor = ContextCompat.getColor(ctx, R.color.text_primary)
        val mutedColor = ContextCompat.getColor(ctx, R.color.text_muted)

        if (!isFindFriends) {
            binding.btnTypeOpponent.setBackgroundResource(R.drawable.bg_card)
            binding.btnTypeFriends.setBackgroundColor(Color.TRANSPARENT)
        } else {
            binding.btnTypeFriends.setBackgroundResource(R.drawable.bg_card)
            binding.btnTypeOpponent.setBackgroundColor(Color.TRANSPARENT)
        }

        binding.btnTypeOpponent.setTextColor(if (!isFindFriends) activeColor else mutedColor)
        binding.btnTypeOpponent.setTypeface(null, if (!isFindFriends) Typeface.BOLD else Typeface.NORMAL)

        binding.btnTypeFriends.setTextColor(if (isFindFriends) activeColor else mutedColor)
        binding.btnTypeFriends.setTypeface(null, if (isFindFriends) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun renderLevelChips() {
        val brand = ContextCompat.getColor(requireContext(), R.color.brand)
        val primary = ContextCompat.getColor(requireContext(), R.color.text_primary)
        levelChips.forEachIndexed { idx, chip ->
            val selected = idx == selectedLevelIndex
            chip.backgroundTintList = if (selected) ColorStateList.valueOf(brand) else null
            chip.setTextColor(if (selected) Color.WHITE else primary)
            chip.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    private fun renderPlayersAndCost() {
        val totalPlayers = hostCount + extraPlayers
        val perPerson = if (totalPlayers > 0) courtPriceVnd / totalPlayers else courtPriceVnd

        binding.tvExtraPlayers.text = extraPlayers.toString()
        binding.tvCourtPrice.text = courtPriceVnd.vnd()
        binding.tvTotalPlayers.text = totalPlayers.toString()
        binding.tvPerPerson.text = perPerson.vnd()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}