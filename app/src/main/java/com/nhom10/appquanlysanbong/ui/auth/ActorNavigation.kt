package com.nhom10.appquanlysanbong.ui.auth

import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.data.session.SessionStore
import com.nhom10.appquanlysanbong.ui.admin.AdminHomeFragment
import com.nhom10.appquanlysanbong.ui.courts.CourtListFragment
import com.nhom10.appquanlysanbong.ui.guest.GuestHomeFragment
import com.nhom10.appquanlysanbong.ui.management.ManagerHomeFragment
import com.nhom10.appquanlysanbong.ui.reception.ReceptionHomeFragment

/** Route gốc theo phiên để Back không quay lại màn của tài khoản cũ sau khi đăng xuất. */
fun Fragment.openActorHome() {
    val home = when (SessionStore(requireContext()).current.role) {
        Role.GUEST -> GuestHomeFragment()
        Role.CUSTOMER -> CourtListFragment()
        Role.RECEPTION -> ReceptionHomeFragment()
        Role.MANAGER -> ManagerHomeFragment()
        Role.ADMIN -> AdminHomeFragment()
    }
    (requireActivity() as Navigator).navigateAsRoot(home)
}
