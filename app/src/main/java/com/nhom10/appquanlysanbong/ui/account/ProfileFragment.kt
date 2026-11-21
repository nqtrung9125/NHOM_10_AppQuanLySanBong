package com.nhom10.appquanlysanbong.ui.account

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
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.data.session.SessionStore
import com.nhom10.appquanlysanbong.databinding.FragmentProfileBinding
import com.nhom10.appquanlysanbong.databinding.ItemChipBinding
import com.nhom10.appquanlysanbong.ui.common.toastComingSoon

/** KH-15 Thông tin cá nhân: xem và cập nhật hồ sơ, VNeID và trình độ chơi. */
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val skillOptions = listOf("Vui vẻ", "Trung bình", "Khá", "Mạnh")
    private var selectedSkillIndex = DemoData.DEFAULT_LEVEL.coerceIn(0, skillOptions.lastIndex)
    private val skillChips = mutableListOf<TextView>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        binding.appbar.tvAppbarTitle.text = "Thông tin cá nhân"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val actor = SessionStore(requireContext()).current
        binding.etFullName.setText(actor.name)
        binding.tvLockedPhone.text = actor.phone
        binding.tvProfileAvatar.text = actor.name.substringAfterLast(' ').take(1).uppercase()

        binding.btnChangeAvatar.setOnClickListener { toastComingSoon() }
        binding.tvVneidStatus.setOnClickListener {
            Toast.makeText(requireContext(), "Tài khoản VNeID đã được xác thực", Toast.LENGTH_SHORT).show()
        }

        buildSkillChips()
        renderSkillChips()

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etFullName.text?.toString()?.trim().orEmpty()
            if (name.isEmpty()) {
                binding.etFullName.error = "Vui lòng nhập họ và tên"
                return@setOnClickListener
            }
            binding.tvProfileAvatar.text = name.lastOrNull()?.uppercase() ?: "T"
            Toast.makeText(
                requireContext(),
                "Đã lưu thông tin (${skillOptions[selectedSkillIndex]})",
                Toast.LENGTH_SHORT
            ).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun buildSkillChips() {
        binding.llSkillLevels.removeAllViews()
        skillChips.clear()
        skillOptions.forEachIndexed { index, label ->
            val chip = ItemChipBinding.inflate(layoutInflater, binding.llSkillLevels, false).root
            chip.text = label
            (chip.layoutParams as LinearLayout.LayoutParams).marginStart = if (index == 0) 0 else dp(8)
            chip.setOnClickListener {
                selectedSkillIndex = index
                renderSkillChips()
            }
            binding.llSkillLevels.addView(chip)
            skillChips.add(chip)
        }
    }

    private fun renderSkillChips() {
        val brand = ContextCompat.getColor(requireContext(), R.color.brand)
        val primary = ContextCompat.getColor(requireContext(), R.color.text_primary)
        skillChips.forEachIndexed { idx, chip ->
            val selected = idx == selectedSkillIndex
            chip.backgroundTintList = if (selected) ColorStateList.valueOf(brand) else null
            chip.setTextColor(if (selected) Color.WHITE else primary)
            chip.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
