package com.nhom10.appquanlysanbong.ui.account

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentChangePasswordBinding

/** KH-16 Đổi mật khẩu: kiểm tra độ mạnh mật khẩu theo thời gian thực và xác nhận cập nhật. */
class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangePasswordBinding.bind(view)

        binding.appbar.tvAppbarTitle.text = "Đổi mật khẩu"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                evaluateStrength(s?.toString().orEmpty())
            }
        })
        evaluateStrength("")

        binding.btnUpdatePassword.setOnClickListener {
            val currentPass = binding.etCurrentPassword.text?.toString().orEmpty()
            val newPass = binding.etNewPassword.text?.toString().orEmpty()
            val confirmPass = binding.etConfirmPassword.text?.toString().orEmpty()

            if (currentPass.isEmpty()) {
                binding.etCurrentPassword.error = "Nhập mật khẩu hiện tại"
                return@setOnClickListener
            }
            if (newPass.length < 6) {
                binding.etNewPassword.error = "Mật khẩu mới tối thiểu 6 ký tự"
                return@setOnClickListener
            }
            if (newPass != confirmPass) {
                binding.etConfirmPassword.error = "Mật khẩu nhập lại không khớp"
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "Đã cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun evaluateStrength(password: String) {
        val hasLength = password.length >= 6
        val hasUpper = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasAlnum = hasUpper && hasDigit
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        val ctx = requireContext()
        val activeColor = ContextCompat.getColor(ctx, R.color.brand)
        val inactiveColor = ContextCompat.getColor(ctx, R.color.text_muted)
        val barInactive = ContextCompat.getColor(ctx, R.color.border_card)

        binding.tvRuleLengthIcon.setTextColor(if (hasLength) activeColor else inactiveColor)
        binding.tvRuleLength.setTextColor(if (hasLength) activeColor else inactiveColor)

        binding.tvRuleAlnumIcon.setTextColor(if (hasAlnum) activeColor else inactiveColor)
        binding.tvRuleAlnum.setTextColor(if (hasAlnum) activeColor else inactiveColor)

        binding.tvRuleSpecialIcon.setTextColor(if (hasSpecial) activeColor else inactiveColor)
        binding.tvRuleSpecial.setTextColor(if (hasSpecial) activeColor else inactiveColor)

        val score = listOf(hasLength, hasUpper, hasDigit, hasSpecial).count { it }
        val (label, activeBars) = when {
            password.isEmpty() -> "Yếu" to 0
            score <= 1 -> "Yếu" to 1
            score == 2 -> "Trung bình" to 2
            score == 3 -> "Khá" to 3
            else -> "Mạnh" to 4
        }

        binding.tvStrengthLabel.text = label
        val bars = listOf(binding.bar1, binding.bar2, binding.bar3, binding.bar4)
        bars.forEachIndexed { index, bar ->
            val tint = if (index < activeBars) activeColor else barInactive
            bar.backgroundTintList = ColorStateList.valueOf(tint)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}