package com.nhom10.appquanlysanbong.ui.pass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.PassData
import com.nhom10.appquanlysanbong.data.PassItem
import com.nhom10.appquanlysanbong.databinding.FragmentPassExpiredBinding
import com.nhom10.appquanlysanbong.ui.courts.CourtListFragment

/** KH-27 Tin pass đã hết hạn. */
class PassExpiredFragment : Fragment(R.layout.fragment_pass_expired) {

    private var _binding: FragmentPassExpiredBinding? = null
    private val binding get() = _binding!!

    private lateinit var passItem: PassItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments?.getString(ARG_CODE) ?: "PS2026101700003"
        passItem = PassData.samplePasses.firstOrNull { it.passCode == code }
            ?: PassItem(
                passCode = code,
                courtName = "Sân B1 · Pickleball",
                sportLabel = "Pickleball",
                date = "Thứ Bảy, 17/10/2026",
                time = "19:00 – 20:30",
                totalVnd = 675_000,
                depositVnd = 202_500,
                remainVnd = 472_500,
                timerBadge = "Hết hạn",
                courtType = "Pickleball · Sơn acrylic",
                duration = "1.5 giờ",
                poster = "Nguyễn Văn H.",
                deadline = "17:00 · 17/10/2026"
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPassExpiredBinding.bind(view)

        val navigator = requireActivity() as Navigator

        binding.tvCourt.text = passItem.courtName
        binding.tvDatetime.text = "${passItem.date} · ${passItem.time}"
        binding.tvPassCode.text = passItem.passCode

        binding.btnOtherPass.setOnClickListener {
            navigator.navigateAsRoot(PassListFragment())
        }
        binding.btnHome.setOnClickListener {
            navigator.navigateAsRoot(CourtListFragment())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_CODE = "pass_code"
        fun newInstance(code: String) = PassExpiredFragment().apply {
            arguments = Bundle().apply { putString(ARG_CODE, code) }
        }
    }
}
