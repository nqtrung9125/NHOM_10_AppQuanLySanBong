package com.nhom10.appquanlysanbong.ui.auth

import android.content.res.ColorStateList
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
import com.nhom10.appquanlysanbong.data.session.DemoAccountRepository
import com.nhom10.appquanlysanbong.databinding.FragmentRegisterBinding
import com.nhom10.appquanlysanbong.databinding.ItemChipBinding

/** KH-03 Đăng ký (demo: bỏ trống cả form thì dùng dữ liệu mẫu). */
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var level = DemoData.DEFAULT_LEVEL
    private val chips = mutableListOf<TextView>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        binding.appbar.tvAppbarTitle.setText(R.string.register_title)
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        chips.clear()
        DemoData.LEVELS.forEachIndexed { index, name ->
            val chip = ItemChipBinding.inflate(layoutInflater, binding.levelContainer, false).root
            chip.text = name
            (chip.layoutParams as LinearLayout.LayoutParams).marginStart = if (index == 0) 0 else dp(8)
            chip.setOnClickListener {
                level = index
                renderLevels()
            }
            binding.levelContainer.addView(chip)
            chips.add(chip)
        }
        renderLevels()

        binding.btnVneid.setOnClickListener {
            Toast.makeText(requireContext(), R.string.msg_vneid_demo, Toast.LENGTH_SHORT).show()
        }
        binding.btnRegister.setOnClickListener { submit() }
    }

    private fun renderLevels() {
        val brand = ContextCompat.getColor(requireContext(), R.color.brand)
        chips.forEachIndexed { i, chip ->
            val on = i == level
            chip.backgroundTintList = if (on) ColorStateList.valueOf(brand) else null
            chip.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (on) android.R.color.white else R.color.text_primary
                )
            )
        }
    }

    private fun submit() {
        val name = binding.edtFullName.text.toString().trim()
        val phone = binding.edtRegPhone.text.toString().trim()
        val pass = binding.edtRegPassword.text.toString()
        val confirm = binding.edtConfirmPassword.text.toString()

        // Demo: bỏ trống hết thì đi tiếp với số điện thoại mẫu
        if (name.isEmpty() && phone.isEmpty() && pass.isEmpty() && confirm.isEmpty()) {
            (requireActivity() as Navigator).navigateTo(OtpFragment.newInstance(DemoData.PHONE))
            return
        }
        if (name.isEmpty() || DemoAccountRepository.normalize(phone).length !in 9..11 || pass.length < 6 || pass != confirm) {
            Toast.makeText(requireContext(), R.string.msg_register_invalid, Toast.LENGTH_SHORT).show()
            return
        }
        if (DemoAccountRepository(requireContext()).find(phone) != null) {
            Toast.makeText(requireContext(), "Số này đã đăng ký. Hãy dùng Đăng nhập.", Toast.LENGTH_SHORT).show()
            return
        }
        val proof = DemoAccountRepository.registrationProof(phone, pass)
        (requireActivity() as Navigator).navigateTo(OtpFragment.newInstance(phone, name, proof))
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        chips.clear()
        _binding = null
        super.onDestroyView()
    }
}
