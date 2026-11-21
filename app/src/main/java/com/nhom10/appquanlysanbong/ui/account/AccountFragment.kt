package com.nhom10.appquanlysanbong.ui.account

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.data.booking.BookingRepository
import com.nhom10.appquanlysanbong.data.booking.ReservationState
import com.nhom10.appquanlysanbong.data.booking.ScheduleDays
import com.nhom10.appquanlysanbong.data.matchmaking.MatchRequestState
import com.nhom10.appquanlysanbong.data.matchmaking.MatchmakingRepository
import com.nhom10.appquanlysanbong.databinding.FragmentAccountBinding
import com.nhom10.appquanlysanbong.data.session.SessionStore
import com.nhom10.appquanlysanbong.ui.auth.openActorHome
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab
import com.nhom10.appquanlysanbong.ui.history.BookingHistoryFragment
import com.nhom10.appquanlysanbong.ui.notifications.NotificationsFragment

/** KH-14 Tài khoản: hồ sơ người dùng, thống kê, lối tắt chức năng và đăng xuất. */
class AccountFragment : Fragment(R.layout.fragment_account) {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccountBinding.bind(view)

        val navigator = requireActivity() as Navigator
        BottomNav.setup(binding.bottomNav, NavTab.ACCOUNT, navigator)

        val session = SessionStore(requireContext())
        val fullName = session.current.name
        binding.tvUserName.text = fullName
        binding.tvUserPhone.text = session.current.phone
        binding.tvAvatarLetter.text = fullName.substringAfterLast(' ').take(1).uppercase()
        val bookings = BookingRepository(requireContext()).mine()
        val now = System.currentTimeMillis()
        binding.tvBookingCount.text = bookings.size.toString()
        binding.tvUpcomingCount.text = bookings.count {
            it.state in setOf(ReservationState.PAID, ReservationState.HELD, ReservationState.PASSING) &&
                ScheduleDays.startMillis(it.day, it.start) > now
        }.toString()
        binding.tvMatchCount.text = MatchmakingRepository(requireContext()).mine().count {
            it.state == MatchRequestState.ACCEPTED
        }.toString()

        val unreadCount = DemoData.notifications.count { it.isUnread }
        if (unreadCount > 0) {
            binding.tvUnreadBadge.visibility = View.VISIBLE
            binding.tvUnreadBadge.text = unreadCount.toString()
        } else {
            binding.tvUnreadBadge.visibility = View.GONE
        }

        binding.btnViewProfile.setOnClickListener {
            navigator.navigateTo(ProfileFragment())
        }
        binding.rowPersonalInfo.setOnClickListener {
            navigator.navigateTo(ProfileFragment())
        }
        binding.rowChangePassword.setOnClickListener {
            navigator.navigateTo(ChangePasswordFragment())
        }

        binding.rowHistory.setOnClickListener {
            navigator.navigateAsRoot(BookingHistoryFragment())
        }
        binding.rowCancelRefund.setOnClickListener {
            navigator.navigateAsRoot(BookingHistoryFragment())
        }
        binding.rowNotifications.setOnClickListener {
            navigator.navigateTo(NotificationsFragment())
        }

        binding.rowLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất khỏi tài khoản hiện tại?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Đăng xuất") { _, _ ->
                    session.signOut()
                    openActorHome()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
