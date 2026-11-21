package com.nhom10.appquanlysanbong.data.matchmaking

import android.content.Context
import com.nhom10.appquanlysanbong.data.Sport
import com.nhom10.appquanlysanbong.data.local.LocalRecords
import com.nhom10.appquanlysanbong.data.local.json
import com.nhom10.appquanlysanbong.data.session.DemoAccounts
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.data.session.SessionStore
import java.util.UUID

enum class MatchRequestState(val label: String) {
    WAITING("Chờ lễ tân"),
    PROPOSED("Đã có đề xuất"),
    ACCEPTED("Đã xác nhận kèo"),
    DECLINED("Đã từ chối")
}

data class MatchRequest(
    val id: String, val owner: String, val customer: String, val sport: Sport,
    val preference: String, val state: MatchRequestState, val proposal: String = ""
)

class MatchmakingRepository(context: Context) {
    private val records = LocalRecords(context)
    private val session = SessionStore(context)

    fun mine() = read().filter { it.owner == session.current.id && session.current.role == Role.CUSTOMER }

    fun requests(): List<MatchRequest> {
        require(session.current.role in setOf(Role.RECEPTION, Role.MANAGER)) { "Bạn không có quyền xem yêu cầu." }
        return read()
    }

    internal fun ensureStaffExamples() = synchronized(lock) {
        val existing = requests()
        val (first, second) = DemoAccounts.accounts.filter { it.role == Role.CUSTOMER }
        val samples = listOf(
            MatchRequest("MAU-CAU-LONG", first.id, first.name, Sport.BADMINTON,
                "Yêu cầu mẫu · Tìm bạn đánh đôi sau 18:00, trình độ trung bình.", MatchRequestState.WAITING),
            MatchRequest("MAU-PICKLEBALL", second.id, second.name, Sport.PICKLEBALL,
                "Yêu cầu mẫu · Tìm nhóm chơi cuối tuần, ưu tiên sân trong nhà.", MatchRequestState.WAITING)
        ).filter { sample -> existing.none { it.id == sample.id } }
        if (samples.isNotEmpty()) write(existing + samples)
    }

    fun create(sport: Sport, preference: String) = synchronized(lock) {
        val user = session.current
        require(user.role == Role.CUSTOMER) { "Hãy đăng nhập bằng tài khoản khách hàng." }
        require(preference.isNotBlank()) { "Nhập khung giờ và nhu cầu cáp kèo." }
        val request = MatchRequest(UUID.randomUUID().toString(), user.id, user.name, sport,
            preference.trim(), MatchRequestState.WAITING)
        write(listOf(request) + read())
    }

    fun propose(id: String, proposal: String) = update(id) {
        require(session.current.role == Role.RECEPTION) { "Chỉ lễ tân được đề xuất kèo." }
        require(it.state == MatchRequestState.WAITING && proposal.isNotBlank()) { "Điền thông tin kèo cho yêu cầu đang chờ." }
        it.copy(state = MatchRequestState.PROPOSED, proposal = proposal.trim())
    }

    fun respond(id: String, accept: Boolean) = update(id) {
        require(session.current.role == Role.CUSTOMER && session.current.id == it.owner) {
            "Yêu cầu thuộc về khách hàng khác."
        }
        require(it.state == MatchRequestState.PROPOSED) { "Yêu cầu chưa có đề xuất hoặc đã xử lý." }
        it.copy(state = if (accept) MatchRequestState.ACCEPTED else MatchRequestState.DECLINED)
    }

    private fun update(id: String, change: (MatchRequest) -> MatchRequest) = synchronized(lock) {
        val list = read()
        val request = requireNotNull(list.find { it.id == id }) { "Không tìm thấy yêu cầu." }
        val changed = change(request)
        write(list.map { if (it.id == id) changed else it })
    }

    private fun read() = records.read("matches") {
        MatchRequest(it.getString("id"), it.getString("owner"), it.getString("customer"),
            Sport.valueOf(it.getString("sport")), it.getString("preference"),
            MatchRequestState.valueOf(it.getString("state")), it.optString("proposal"))
    }

    private fun write(list: List<MatchRequest>) = records.write("matches", list) {
        json("id" to it.id, "owner" to it.owner, "customer" to it.customer, "sport" to it.sport.name,
            "preference" to it.preference, "state" to it.state.name, "proposal" to it.proposal)
    }

    companion object {
        private val lock = Any()
    }
}
