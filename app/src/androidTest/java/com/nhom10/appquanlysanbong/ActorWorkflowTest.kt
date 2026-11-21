package com.nhom10.appquanlysanbong

import android.content.Context
import android.content.ContextWrapper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nhom10.appquanlysanbong.data.Sport
import com.nhom10.appquanlysanbong.data.booking.BookingRepository
import com.nhom10.appquanlysanbong.data.booking.BookingRules
import com.nhom10.appquanlysanbong.data.booking.DepositMethod
import com.nhom10.appquanlysanbong.data.booking.ReservationState
import com.nhom10.appquanlysanbong.data.booking.ScheduleDays
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.data.courts.toEdit
import com.nhom10.appquanlysanbong.data.matchmaking.MatchRequestState
import com.nhom10.appquanlysanbong.data.matchmaking.MatchmakingRepository
import com.nhom10.appquanlysanbong.data.session.DemoAccountRepository
import com.nhom10.appquanlysanbong.data.session.DemoAccounts
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.data.session.SessionStore
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ActorWorkflowTest {
    private val device = InstrumentationRegistry.getInstrumentation().targetContext
    private val prefix = "test_${UUID.randomUUID()}"
    private val prefNames = mutableSetOf<String>()
    private val context = object : ContextWrapper(device) {
        override fun getSharedPreferences(name: String, mode: Int) =
            super.getSharedPreferences("${prefix}_$name".also { prefNames += it }, mode)
    }
    private val session = SessionStore(context)
    private var now = ScheduleDays.startMillis("2026-11-20", 9)
    private val bookings = BookingRepository(context) { now }
    private val catalog = CourtCatalog(context)
    private val friday = "2026-11-20"
    private val saturday = "2026-11-21"

    @After
    fun removeIsolatedData() {
        prefNames.forEach { device.deleteSharedPreferences(it) }
    }

    @Test
    fun staffExamplesKeepExistingReservationsAndSharedUpdates() {
        assertThrows(IllegalArgumentException::class.java) { bookings.ensureStaffExamples() }
        signIn(Role.CUSTOMER)
        now -= 60_000
        val alreadyBooked = bookings.hold("A1", friday, 9, 10)
        bookings.payDeposit(alreadyBooked.id)
        now += 60_000

        signIn(Role.RECEPTION)
        bookings.ensureStaffExamples()
        val initial = bookings.operations()
        assertTrue(initial.any { it.id == alreadyBooked.id && it.state == ReservationState.PAID })
        val inUse = initial.single { it.state == ReservationState.CHECKED_IN }
        assertNotEquals(alreadyBooked.courtId, inUse.courtId)
        assertEquals(ReservationState.COMPLETED, bookings.checkOut(inUse.id).state)

        val matchmaking = MatchmakingRepository(context)
        matchmaking.ensureStaffExamples()
        val request = matchmaking.requests().first()
        matchmaking.propose(request.id, "Đánh đôi lúc 18:00")

        signIn(Role.MANAGER)
        bookings.ensureStaffExamples()
        matchmaking.ensureStaffExamples()
        val updated = bookings.operations()
        assertEquals(initial.map { it.id }, updated.map { it.id })
        assertEquals(ReservationState.COMPLETED, updated.single { it.id == inUse.id }.state)
        val refundRequest = updated.single { it.state == ReservationState.REFUND_REQUESTED }
        assertEquals(ReservationState.REFUNDED, bookings.refund(refundRequest.id).state)
        assertEquals(MatchRequestState.PROPOSED, matchmaking.requests().single { it.id == request.id }.state)
        assertEquals(2, matchmaking.requests().size)
    }

    @Test
    fun guestCanReadButOnlyReceptionAndCustomerCanHold() {
        assertEquals(Role.GUEST, session.current.role)
        assertTrue(bookings.isFree("D2", friday, 18, 19))
        assertThrows(IllegalArgumentException::class.java) { bookings.hold("D2", friday, 18, 19) }
        val customer = DemoAccounts.accounts.first()
        signIn(Role.RECEPTION)
        val hold = bookings.hold("D2", friday, 18, 19, customer)
        assertEquals(800_000, hold.total)
        assertEquals(240_000, hold.deposit)
        assertFalse(bookings.isFree("D2", friday, 18, 19))
        assertTrue(bookings.isFree("D2", friday, 19, 20))

        signIn(Role.CUSTOMER)
        assertEquals(hold.id, BookingRepository(context) { now }.mine().single().id)
        assertThrows(IllegalArgumentException::class.java) { bookings.hold("D2", friday, 18, 19) }
        bookings.payDeposit(hold.id, DepositMethod.TRANSFER)
        assertThrows(IllegalArgumentException::class.java) { bookings.checkIn(hold.id) }

        signIn(Role.RECEPTION)
        val receipt = BookingRepository(context) { now }.operations().single { it.id == hold.id }
        assertEquals("D2", receipt.courtId)
        assertEquals(DepositMethod.TRANSFER, receipt.depositMethod)
        now = ScheduleDays.startMillis(friday, 18)
        assertEquals(ReservationState.CHECKED_IN, bookings.checkIn(hold.id).state)
        assertEquals(ReservationState.COMPLETED, bookings.checkOut(hold.id).state)
    }

    @Test
    fun receptionCanCheckInNearStartOnlyAfterThePreviousCustomerReturnsTheCourt() {
        signIn(Role.CUSTOMER)
        val first = bookings.hold("D2", friday, 18, 19)
        val next = bookings.hold("D2", friday, 19, 20)
        bookings.payDeposit(first.id)
        bookings.payDeposit(next.id)

        signIn(Role.RECEPTION)
        now = ScheduleDays.startMillis(friday, 18) - BookingRules.CHECK_IN_EARLY_MINUTES * 60_000L - 1
        assertThrows(IllegalArgumentException::class.java) { bookings.checkIn(first.id) }
        now += 1
        assertEquals(ReservationState.CHECKED_IN, bookings.checkIn(first.id).state)

        now = ScheduleDays.startMillis(friday, 19)
        assertThrows(IllegalArgumentException::class.java) { BookingRepository(context) { now }.checkIn(next.id) }
        assertEquals(ReservationState.COMPLETED, bookings.checkOut(first.id).state)
        now = ScheduleDays.startMillis(friday, 20)
        assertThrows(IllegalArgumentException::class.java) { bookings.checkIn(next.id) }
        now -= 1
        assertEquals(ReservationState.CHECKED_IN, bookings.checkIn(next.id).state)
    }

    @Test
    fun expiredHoldAndRefundReleaseSlot() {
        signIn(Role.CUSTOMER)
        val hold = bookings.hold("B1", saturday, 18, 19)
        now += 15 * 60_000L
        assertTrue(bookings.isFree("B1", saturday, 18, 19))
        assertEquals(ReservationState.EXPIRED, bookings.mine().single().state)
        assertThrows(IllegalArgumentException::class.java) { bookings.payDeposit(hold.id) }
        val rebook = bookings.hold("B1", saturday, 18, 19)
        bookings.payDeposit(rebook.id)
        assertEquals(ReservationState.REFUND_REQUESTED, bookings.cancel(rebook.id).state)
        assertThrows(IllegalArgumentException::class.java) { bookings.refund(rebook.id) }
        signIn(Role.MANAGER)
        assertEquals(ReservationState.REFUNDED, bookings.refund(rebook.id).state)
        assertTrue(bookings.isFree("B1", saturday, 18, 19))
    }

    @Test
    fun passTransfersExistingSlotToAnotherCustomer() {
        signIn(Role.CUSTOMER)
        val hold = bookings.hold("A1", saturday, 18, 19)
        val paid = bookings.payDeposit(hold.id)
        bookings.postPass(paid.id, paid.deposit)
        assertThrows(IllegalArgumentException::class.java) { bookings.takePass(paid.id) }
        val buyer = DemoAccounts.accounts[1]
        assertTrue(session.signIn(buyer.phone))
        assertThrows(IllegalArgumentException::class.java) { bookings.cancel(paid.id) }
        val received = bookings.takePass(paid.id)
        assertEquals(paid.id, received.id)
        assertEquals(buyer.id, bookings.mine().single().ownerId)
        assertTrue(bookings.market().isEmpty())
        assertFalse(bookings.isFree("A1", saturday, 18, 19))
        signIn(Role.CUSTOMER)
        assertEquals(ReservationState.TRANSFERRED, bookings.mine().single().state)
    }

    @Test
    fun courtChangesAndMatchProposalAreShared() {
        signIn(Role.CUSTOMER)
        val court = checkNotNull(catalog.find("D3")).toEdit()
        assertThrows(IllegalArgumentException::class.java) { catalog.save(court) }
        val matchmaking = MatchmakingRepository(context)
        matchmaking.create(Sport.PICKLEBALL, "18h, trình độ trung bình")
        val request = matchmaking.mine().single()
        assertThrows(IllegalArgumentException::class.java) { matchmaking.propose(request.id, "Sân B1") }

        signIn(Role.RECEPTION)
        assertEquals(request.id, matchmaking.requests().single().id)
        matchmaking.propose(request.id, "Đội An, sân B1 lúc 18h")
        signIn(Role.CUSTOMER)
        matchmaking.respond(request.id, true)
        assertEquals(MatchRequestState.ACCEPTED, matchmaking.mine().single().state)

        signIn(Role.ADMIN)
        catalog.save(court.copy(day = 950_000, evening = 1_300_000, weekend = 1_500_000))
        val saved = checkNotNull(CourtCatalog(context).find("D3"))
        assertEquals(2_250_000, BookingRules.quote(saved, friday, 15, 17))
        assertEquals(1_500_000, BookingRules.quote(saved, saturday, 17, 18))
        catalog.save(saved.toEdit().copy(active = false))
        assertFalse(bookings.isFree("D3", saturday, 17, 18))
    }

    @Test
    fun registeredUserCanSignInAgainAsCustomer() {
        val phone = "0908301234"
        val password = "android-demo"
        val proof = DemoAccountRepository.registrationProof(phone, password)
        assertTrue(session.registerAndSignIn(phone, "Khách mới", proof))
        session.signOut()
        assertEquals(Role.GUEST, session.current.role)
        val identity = DemoAccountRepository(context)
        assertFalse(identity.accepts(phone, "wrong"))
        assertTrue(identity.accepts(phone, password))
        assertTrue(session.signIn(phone))
        assertEquals(Role.CUSTOMER, session.current.role)
        assertEquals("Khách mới", session.current.name)
    }

    @Test
    fun profileAndPasswordChangesPersistForDemoAndRegisteredCustomers() {
        val registeredPhone = "0908301234"
        val initialPassword = "android-demo"
        assertTrue(session.registerAndSignIn(
            registeredPhone, "Khách mới", DemoAccountRepository.registrationProof(registeredPhone, initialPassword)
        ))
        val demo = DemoAccounts.accounts.first()
        val identity = DemoAccountRepository(context)
        val savedName = "Khách đã cập nhật"
        val replacement = "MatKhauMoi1"
        for ((phone, original) in listOf(demo.phone to DemoAccounts.PASSWORD, registeredPhone to initialPassword)) {
            assertTrue(identity.updateProfile(phone, savedName, 2))
            assertFalse(identity.changePassword(phone, "wrong", replacement))
            assertTrue(identity.accepts(phone, original))
            assertTrue(identity.changePassword(phone, original, replacement))
            session.signOut()

            val reopened = DemoAccountRepository(context)
            assertFalse(reopened.accepts(phone, original))
            assertTrue(reopened.accepts(phone, replacement))
            assertTrue(session.signIn(phone))
            assertEquals(savedName, session.current.name)
            assertEquals(2, session.current.skillLevel)
            assertEquals(Role.CUSTOMER, session.current.role)
        }
        val other = DemoAccounts.accounts[1]
        assertEquals(other, identity.find(other.phone))
        assertTrue(identity.accepts(other.phone, DemoAccounts.PASSWORD))
    }

    private fun signIn(role: Role) {
        assertTrue(session.signIn(DemoAccounts.accounts.first { it.role == role }.phone))
    }
}
