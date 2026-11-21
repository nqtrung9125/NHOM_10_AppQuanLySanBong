package com.nhom10.appquanlysanbong.ui.pass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.PassData
import com.nhom10.appquanlysanbong.data.PassItem
import com.nhom10.appquanlysanbong.databinding.FragmentReceiveFailureBinding
import com.nhom10.appquanlysanbong.ui.courts.CourtListFragment

/** KH-26 Nhận sân chuyển nhượng thất bại (đã có người nhận trước). */
class ReceiveFailureFragment : Fragment(R.layout.fragment_receive_failure) {

    private var _binding: FragmentReceiveFailureBinding? = null
    private val binding get() = _binding!!

    private lateinit var passItem: PassItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments?.getString(ARG_CODE) ?: PassData.samplePasses.last().passCode
        passItem = PassData.samplePasses.firstOrNull { it.passCode == code } ?: PassData.samplePasses.last()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReceiveFailureBinding.bind(view)

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
        fun newInstance(code: String) = ReceiveFailureFragment().apply {
            arguments = Bundle().apply { putString(ARG_CODE, code) }
        }
    }
}
