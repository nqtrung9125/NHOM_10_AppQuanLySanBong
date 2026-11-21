package com.nhom10.appquanlysanbong.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.data.session.DemoAccounts
import com.nhom10.appquanlysanbong.data.session.DemoAccountRepository
import com.nhom10.appquanlysanbong.databinding.FragmentLoginBinding

/** KH-01 Đăng nhập (demo: bỏ trống cả hai ô sẽ dùng tài khoản mẫu). */
class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        arguments?.getString("phone")?.let {
            binding.edtPhone.setText(it)
            if (DemoAccountRepository(requireContext()).accepts(it, DemoAccounts.PASSWORD)) {
                binding.edtPassword.setText(DemoAccounts.PASSWORD)
            }
        }

        binding.btnLogin.setOnClickListener { doLogin() }
        binding.tvRegister.setOnClickListener {
            (requireActivity() as Navigator).navigateTo(RegisterFragment())
        }
        binding.tvForgot.setOnClickListener { comingSoon() }
    }

    private fun doLogin() {
        var phone = binding.edtPhone.text.toString().trim()
        var password = binding.edtPassword.text.toString()

        if (phone.isEmpty() && password.isEmpty()) {
            phone = DemoData.PHONE
            password = DemoData.PASSWORD
        }
        if (!DemoAccountRepository(requireContext()).accepts(phone, password)) {
            Toast.makeText(requireContext(), "Kiểm tra số điện thoại và mật khẩu.", Toast.LENGTH_SHORT).show()
            return
        }
        (requireActivity() as Navigator).navigateTo(OtpFragment.newInstance(phone))
    }

    private fun comingSoon() {
        Toast.makeText(requireContext(), R.string.msg_coming_soon, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(phone: String) = LoginFragment().apply {
            arguments = Bundle().apply { putString("phone", phone) }
        }
    }
}
