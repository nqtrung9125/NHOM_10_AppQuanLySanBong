package com.nhom10.appquanlysanbong.ui.auth

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.databinding.FragmentOtpBinding
import com.nhom10.appquanlysanbong.data.session.DemoAccounts
import com.nhom10.appquanlysanbong.data.session.SessionStore
import java.util.Locale

/** KH-02 Xác thực OTP: 6 ô nhập, tự nhảy ô, đếm ngược gửi lại mã. */
class OtpFragment : Fragment(R.layout.fragment_otp) {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private lateinit var boxes: List<EditText>
    private var timer: CountDownTimer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOtpBinding.bind(view)

        val phone = arguments?.getString(ARG_PHONE) ?: DemoData.PHONE
        binding.tvOtpSubtitle.text = "Xác thực $phone bằng mã OTP demo ${DemoAccounts.OTP}."

        boxes = with(binding) { listOf(otp1, otp2, otp3, otp4, otp5, otp6) }
        setupBoxes()

        // Dữ liệu mặc định giống Figma: đã điền sẵn "4 8 2"
        DemoData.OTP_PREFILL.forEachIndexed { i, c -> boxes[i].setText(c.toString()) }
        boxes[DemoData.OTP_PREFILL.length].requestFocus()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnConfirm.setOnClickListener { confirm() }
        binding.tvResend.setOnClickListener {
            if (timer == null) {
                Toast.makeText(requireContext(), R.string.otp_resent, Toast.LENGTH_SHORT).show()
                startCountdown()
            }
        }
        startCountdown()
    }

    private fun setupBoxes() {
        boxes.forEachIndexed { index, box ->
            box.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
                override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < boxes.lastIndex) boxes[index + 1].requestFocus()
                }
            })
            box.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN &&
                    box.text.isEmpty() && index > 0
                ) {
                    boxes[index - 1].setText("")
                    boxes[index - 1].requestFocus()
                    true
                } else false
            }
        }
    }

    private fun confirm() {
        val code = boxes.joinToString("") { it.text.toString() }
        if (code.length < boxes.size) {
            Toast.makeText(requireContext(), R.string.otp_incomplete, Toast.LENGTH_SHORT).show()
            return
        }
        if (code != DemoAccounts.OTP) {
            Toast.makeText(requireContext(), "Mã OTP demo không đúng.", Toast.LENGTH_SHORT).show()
            return
        }
        val phone = arguments?.getString(ARG_PHONE) ?: DemoData.PHONE
        val name = arguments?.getString("registered_name")
        val session = SessionStore(requireContext())
        val signedIn = if (name == null) session.signIn(phone) else {
            session.registerAndSignIn(phone, name, arguments?.getString("registered_proof").orEmpty())
        }
        if (!signedIn) {
            Toast.makeText(requireContext(), "Không tìm thấy tài khoản demo.", Toast.LENGTH_SHORT).show()
            return
        }
        openActorHome()
    }

    private fun startCountdown() {
        timer?.cancel()
        binding.tvResend.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted))
        timer = object : CountDownTimer(DemoData.OTP_RESEND_SECONDS * 1000L, 1000) {
            override fun onTick(ms: Long) {
                val sec = ms / 1000
                _binding?.tvResend?.text = getString(
                    R.string.otp_resend_countdown,
                    String.format(Locale.getDefault(), "%02d:%02d", sec / 60, sec % 60)
                )
            }

            override fun onFinish() {
                timer = null
                _binding?.tvResend?.apply {
                    setText(R.string.otp_resend)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.brand))
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        timer?.cancel()
        timer = null
        _binding = null
        boxes = emptyList()
        super.onDestroyView()
    }

    companion object {
        private const val ARG_PHONE = "phone"

        fun newInstance(phone: String, registeredName: String? = null, proof: String? = null) = OtpFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PHONE, phone)
                putString("registered_name", registeredName)
                putString("registered_proof", proof)
            }
        }
    }
}
