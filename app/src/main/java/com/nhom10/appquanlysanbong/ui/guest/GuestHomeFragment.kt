package com.nhom10.appquanlysanbong.ui.guest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.Court
import com.nhom10.appquanlysanbong.data.Sport
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.data.courts.displayName
import com.nhom10.appquanlysanbong.data.session.DemoAccounts
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.data.session.SessionStore
import com.nhom10.appquanlysanbong.databinding.FragmentGuestCourtListBinding
import com.nhom10.appquanlysanbong.databinding.ItemCourtBinding
import com.nhom10.appquanlysanbong.ui.auth.LoginFragment
import com.nhom10.appquanlysanbong.ui.auth.RegisterFragment
import com.nhom10.appquanlysanbong.ui.auth.openActorHome
import com.nhom10.appquanlysanbong.ui.common.tintBackground
import com.nhom10.appquanlysanbong.ui.courts.CourtDetailFragment

/** VL-01. Bố cục của bản Downloads đọc danh mục sân chung qua CourtCatalog. */
class GuestHomeFragment : Fragment(R.layout.fragment_guest_court_list) {
    private var binding: FragmentGuestCourtListBinding? = null
    private val navigator get() = requireActivity() as Navigator
    private var selectedSport: Sport? = null
    private var query = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (SessionStore(requireContext()).current.role != Role.GUEST) {
            openActorHome()
            return
        }
        if (savedInstanceState != null) {
            selectedSport = Sport.entries.find { it.name == savedInstanceState.getString("sport") }
            query = savedInstanceState.getString("query").orEmpty()
        }

        val screen = FragmentGuestCourtListBinding.bind(view)
        binding = screen
        with(screen) {
            appbar.tvAppbarTitle.setText(R.string.brand_name)
            appbar.btnLogin.isVisible = true
            appbar.btnLogin.setOnClickListener { navigator.navigateTo(LoginFragment()) }
            actions.btnOutline.setOnClickListener { navigator.navigateTo(LoginFragment()) }
            actions.btnPrimary.setText(R.string.register)
            actions.btnPrimary.setOnClickListener { navigator.navigateTo(RegisterFragment()) }
            actions.btnDemoAccounts.setOnClickListener { selectDemoAccount() }
            guestSearch.setText(query)
            guestSearch.doAfterTextChanged {
                query = it?.toString().orEmpty()
                render()
            }
            cardFootball.setOnClickListener {
                selectedSport = Sport.FOOTBALL
                render()
            }
            filters(screen).forEach { (sport, chip) ->
                chip.setOnClickListener {
                    selectedSport = sport
                    render()
                }
            }
        }
        render()
    }

    private fun filters(screen: FragmentGuestCourtListBinding) = with(screen) {
        linkedMapOf(null to chipAll, Sport.FOOTBALL to chipFootball, Sport.BADMINTON to chipBadminton,
            Sport.PICKLEBALL to chipPickleball, Sport.VOLLEYBALL to chipVolleyball)
    }

    private fun render() {
        val screen = binding ?: return
        val search = query.trim()
        val unfiltered = selectedSport == null && search.isEmpty()
        val visible = CourtCatalog(requireContext()).all().filter {
            (selectedSport == null || it.sport == selectedSport) &&
                (it.displayName.contains(search, ignoreCase = true) || it.id.contains(search, ignoreCase = true))
        }
        filters(screen).forEach { (sport, chip) ->
            val selected = sport == selectedSport
            chip.isSelected = selected
            chip.tintBackground(if (selected) R.color.brand else null)
            chip.setTextColor(ContextCompat.getColor(chip.context,
                if (selected) android.R.color.white else R.color.text_primary))
        }
        screen.cardFootball.isVisible = unfiltered && visible.any { it.sport == Sport.FOOTBALL }
        screen.guestEmpty.isVisible = visible.isEmpty()
        screen.llCourts.removeAllViews()
        visible.filterNot { screen.cardFootball.isVisible && it.sport == Sport.FOOTBALL }
            .forEach { court -> screen.llCourts.addView(courtCard(screen, court)) }
    }

    private fun courtCard(screen: FragmentGuestCourtListBinding, court: Court): View =
        ItemCourtBinding.inflate(layoutInflater, screen.llCourts, false).apply {
            ivCourtArt.setImageResource(R.drawable.ic_guest_court)
            tvCourtName.text = court.displayName
            tvCourtDesc.text = court.description
            tvCourtPrice.text = court.priceFrom
            tvCourtStatus.setText(if (court.available) R.string.status_available else R.string.status_in_use)
            tvCourtStatus.tintBackground(if (court.available) R.color.brand_tint else R.color.danger_bg)
            tvCourtStatus.setTextColor(ContextCompat.getColor(root.context,
                if (court.available) R.color.brand else R.color.danger))
            root.setOnClickListener { navigator.navigateTo(CourtDetailFragment.newInstance(court.id)) }
        }.root

    private fun selectDemoAccount() {
        val accounts = DemoAccounts.accounts
        val labels = accounts.map { "${it.role.label} · ${it.name}" }.toTypedArray()
        AlertDialog.Builder(requireContext()).setTitle(R.string.guest_demo_accounts)
            .setItems(labels) { _, index ->
                navigator.navigateTo(LoginFragment.newInstance(accounts[index].phone))
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("sport", selectedSport?.name)
        outState.putString("query", query)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
