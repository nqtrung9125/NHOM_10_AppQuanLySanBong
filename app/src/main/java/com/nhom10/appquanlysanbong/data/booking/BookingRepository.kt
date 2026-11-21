package com.nhom10.appquanlysanbong.data.booking

import android.content.Context
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.data.courts.displayName
import com.nhom10.appquanlysanbong.data.local.LocalRecords
import com.nhom10.appquanlysanbong.data.local.json
import com.nhom10.appquanlysanbong.data.session.Actor
import com.nhom10.appquanlysanbong.data.session.DemoAccountRepository
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.data.session.SessionStore
import java.util.UUID

class BookingRepository(context: Context, private val clock: () -> Long = System::currentTimeMillis) {
    private val records = LocalRecords(context)
    private val session = SessionStore(context)
    private val catalog = CourtCatalog(context)
    private val accounts = DemoAccountRepository(context)

    fun mine(): List<Reservation> {
        val actor = session.current
        return if (actor.role == Role.CUSTOMER) read().filter { it.ownerId == actor.id } else emptyList()
    }

    fun operations(): List<Reservation> {
        requireRole(Role.RECEPTION, Role.MANAGER, Role.ADMIN)
        return read()
    }

    /** Hai vai trò dùng một bộ phiếu mẫu mỗi ngày và giữ lại các thao tác đã thực hiện. */
    internal fun ensureStaffExamples() = synchronized(lock) {
        requireRole(Role.RECEPTION, Role.MANAGER)
        val now = clock()
        val existing = read()
        val prefix = staffBookingExamplePrefix(ScheduleDays.date(now = now))
        if (existing.none { it.id.startsWith(prefix) }) {
            val samples = staffBookingExamples(catalog.all(), existing, now)
            if (samples.isNotEmpty()) write(existing + samples)
        }
    }

    fun market() = read().filter {
        it.state == ReservationState.PASSING && ScheduleDays.startMillis(it.day, it.start) > clock()
    }

    fun isFree(courtId: String, day: String, start: Int, end: Int): Boolean {
        val now = clock()
        return catalog.find(courtId)?.available == true && start in 5..21 && end in (start + 1)..22 &&
            ScheduleDays.startMillis(day, start) > now &&
            read().none { BookingRules.overlaps(it, courtId, day, start, end, now) }
    }

    fun availableHours(courtId: String, day: String): Set<Int> {
        if (catalog.find(courtId)?.available != true) return emptySet()
        val now = clock()
        val reservations = read()
        return (5..21).filterTo(mutableSetOf()) { hour ->
            ScheduleDays.startMillis(day, hour) > now &&
                reservations.none { BookingRules.overlaps(it, courtId, day, hour, hour + 1, now) }
        }
    }

    fun hold(courtId: String, day: String, start: Int, end: Int, customer: Actor? = null): Reservation =
        synchronized(lock) {
            val user = session.current
            val owner = if (customer == null) {
                requireRole(Role.CUSTOMER)
                user
            } else {
                requireRole(Role.RECEPTION)
                require(accounts.find(customer.phone) == customer && customer.role == Role.CUSTOMER) { "Chọn khách hàng." }
                customer
            }
            val court = requireNotNull(catalog.find(courtId)) { "Sân không còn trong danh mục." }
            val total = BookingRules.quote(court, day, start, end)
            require(isFree(courtId, day, start, end)) { "Khung giờ đã qua, sân đóng hoặc có người giữ." }
            val now = clock()
            val hold = Reservation(code(day), owner.id, court.id, court.displayName, day, start, end, total,
                ReservationState.HELD,
                minOf(now + BookingRules.HOLD_MINUTES * 60_000, ScheduleDays.startMillis(day, start)),
                contactName = owner.name)
            write(listOf(hold) + read())
            hold
        }

    fun payDeposit(id: String, method: DepositMethod? = null) = change(id) {
        requireOwnerOrReception(it)
        require(it.state == ReservationState.HELD) { "Suất giữ sân đã hết hạn hoặc đã được thanh toán." }
        require(catalog.find(it.courtId)?.available == true) { "Sân tạm ngưng hoạt động." }
        it.copy(state = ReservationState.PAID, expiresAt = 0, depositMethod = method)
    }

    fun cancel(id: String) = change(id) {
        requireOwnerOrReception(it)
        require(it.state == ReservationState.HELD || it.state == ReservationState.PAID) {
            "Trạng thái hiện tại không thể hủy."
        }
        val remaining = ScheduleDays.startMillis(it.day, it.start) - clock()
        require(remaining > 0) { "Booking đã đến giờ chơi." }
        val state = when {
            it.state == ReservationState.HELD -> ReservationState.CANCELLED
            remaining >= BookingRules.REFUND_HOURS * 3_600_000L -> ReservationState.REFUND_REQUESTED
            else -> ReservationState.CANCELLED
        }
        it.copy(state = state, expiresAt = 0)
    }

    fun checkIn(id: String) = change(id) {
        requireRole(Role.RECEPTION)
        require(it.state == ReservationState.PAID && it.day == ScheduleDays.date(now = clock()) &&
            ScheduleDays.startMillis(it.day, it.end) > clock()) {
            "Chỉ nhận sân cho booking đã cọc trong ngày."
        }
        require(catalog.find(it.courtId)?.available == true) { "Sân tạm ngưng hoạt động." }
        it.copy(state = ReservationState.CHECKED_IN)
    }

    fun checkOut(id: String) = change(id) {
        requireRole(Role.RECEPTION)
        require(it.state == ReservationState.CHECKED_IN) { "Cần nhận sân trước khi trả sân." }
        it.copy(state = ReservationState.COMPLETED)
    }

    fun refund(id: String) = change(id) {
        requireRole(Role.MANAGER)
        require(it.state == ReservationState.REFUND_REQUESTED) { "Booking không có yêu cầu hoàn cọc." }
        it.copy(state = ReservationState.REFUNDED)
    }

    fun postPass(id: String, price: Int) = change(id) {
        requireOwner(it)
        require(it.state == ReservationState.PAID && ScheduleDays.startMillis(it.day, it.start) > clock()) {
            "Chỉ pass được booking đã cọc và chưa đến giờ chơi."
        }
        require(catalog.find(it.courtId)?.available == true) { "Sân tạm ngưng hoạt động." }
        require(price in 1..it.deposit) { "Giá pass phải lớn hơn 0 và tối đa bằng tiền cọc." }
        it.copy(state = ReservationState.PASSING, passPrice = price)
    }

    fun withdrawPass(id: String) = change(id) {
        requireOwner(it)
        require(it.state == ReservationState.PASSING) { "Booking chưa đăng pass." }
        it.copy(state = ReservationState.PAID, passPrice = 0)
    }

    fun takePass(id: String): Reservation = synchronized(lock) {
        requireRole(Role.CUSTOMER)
        val buyer = session.current
        val reservations = read()
        val pass = requireNotNull(reservations.find { it.id == id }) { "Không tìm thấy booking." }
        require(pass.ownerId != buyer.id && pass.state == ReservationState.PASSING &&
            ScheduleDays.startMillis(pass.day, pass.start) > clock() && catalog.find(pass.courtId)?.available == true) {
            "Tin pass đã hết hạn, sân tạm ngưng hoặc do bạn đăng."
        }
        val updated = pass.copy(ownerId = buyer.id, contactName = buyer.name, state = ReservationState.PAID, passPrice = 0)
        val sellerCopy = pass.copy(id = "PASS-${pass.id}-${UUID.randomUUID().toString().take(8)}",
            state = ReservationState.TRANSFERRED, passPrice = 0)
        write(listOf(updated, sellerCopy) + reservations.filterNot { it.id == id })
        updated
    }

    private fun change(id: String, update: (Reservation) -> Reservation): Reservation = synchronized(lock) {
        val reservations = read()
        val old = requireNotNull(reservations.find { it.id == id }) { "Không tìm thấy booking." }
        val updated = update(old)
        write(reservations.map { if (it.id == id) updated else it })
        updated
    }

    private fun requireRole(vararg roles: Role) {
        require(session.current.role in roles) { "Tài khoản hiện tại không có quyền thực hiện." }
    }

    private fun requireOwner(reservation: Reservation) {
        requireRole(Role.CUSTOMER)
        require(reservation.ownerId == session.current.id) { "Booking thuộc về khách hàng khác." }
    }

    private fun requireOwnerOrReception(reservation: Reservation) {
        if (session.current.role != Role.RECEPTION) requireOwner(reservation)
    }

    private fun read(): List<Reservation> {
        val now = clock()
        return records.read("reservations") {
            val saved = Reservation(it.getString("id"), it.getString("owner"), it.getString("court"),
                it.getString("courtName"), it.getString("day"), it.getInt("start"), it.getInt("end"),
                it.getInt("total"), ReservationState.valueOf(it.getString("state")), it.optLong("expires"),
                it.optInt("pass"), it.optString("contact"),
                DepositMethod.entries.find { method -> method.name == it.optString("depositMethod") })
            saved.copy(state = BookingRules.effectiveState(saved, now))
        }
    }

    private fun write(items: List<Reservation>) = records.write("reservations", items) {
        json("id" to it.id, "owner" to it.ownerId, "court" to it.courtId, "courtName" to it.courtName,
            "day" to it.day, "start" to it.start, "end" to it.end, "total" to it.total,
            "state" to it.state.name, "expires" to it.expiresAt, "pass" to it.passPrice,
            "contact" to it.contactName, "depositMethod" to it.depositMethod?.name)
    }

    private fun code(day: String) = "DS${day.replace("-", "")}-${UUID.randomUUID().toString().take(8).uppercase()}"

    companion object {
        // Kiểm tra lịch và ghi booking phải đi cùng nhau, kể cả khi mở từ hai màn hình.
        private val lock = Any()
    }
}
