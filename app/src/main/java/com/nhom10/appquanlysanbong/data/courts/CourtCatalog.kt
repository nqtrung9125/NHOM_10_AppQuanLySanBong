    package com.nhom10.appquanlysanbong.data.courts

import android.content.Context
import com.nhom10.appquanlysanbong.data.Court
import com.nhom10.appquanlysanbong.data.PriceRow
import com.nhom10.appquanlysanbong.data.Sport
import com.nhom10.appquanlysanbong.data.formatVndAmount
import com.nhom10.appquanlysanbong.data.local.LocalRecords
import com.nhom10.appquanlysanbong.data.local.json
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.data.session.SessionStore

class CourtCatalog(context: Context) {
    private val records = LocalRecords(context)
    private val session = SessionStore(context)

    fun all(): List<Court> {
        val edits = readEdits().associateBy(CourtEdit::id)
        return demoCourts.map { edits[it.id]?.toCourt(it) ?: it } +
            edits.values.filter { edit -> demoCourts.none { it.id == edit.id } }.map { it.toCourt(null) }
    }

    fun find(id: String?) = all().find { it.id == id }

    fun save(court: CourtEdit) {
        require(session.current.role == Role.ADMIN) { "Chỉ quản trị viên được sửa sân." }
        require(court.id.matches(Regex("[A-Z0-9]{1,8}")) && court.name.isNotBlank()) {
            "Mã sân gồm 1–8 chữ in hoa hoặc chữ số. Hãy nhập tên sân."
        }
        require(court.players in 1..30 && listOf(court.day, court.evening, court.weekend).all { it in 1_000..10_000_000 }) {
            "Nhập số người và ba mức giá hợp lệ."
        }
        require(court.sport != Sport.FOOTBALL || court.players in setOf(5, 7, 11)) {
            "Sân bóng đá phải có loại 5, 7 hoặc 11 người."
        }
        val updated = readEdits().filter { it.id != court.id } + court
        records.write("court_edits", updated) {
            json("id" to it.id, "name" to it.name, "sport" to it.sport.name, "players" to it.players,
                "day" to it.day, "evening" to it.evening, "weekend" to it.weekend, "active" to it.active)
        }
    }

    private fun readEdits() = records.read("court_edits") {
        CourtEdit(it.getString("id"), it.getString("name"), Sport.valueOf(it.getString("sport")),
            it.getInt("players"), it.getInt("day"), it.getInt("evening"), it.getInt("weekend"),
            it.getBoolean("active"))
    }

    private fun CourtEdit.toCourt(seed: Court?) =
        newCourt(id, name, sport, players, day, evening, weekend, active).let { court ->
            if (seed?.sport == court.sport) court.copy(surface = seed.surface, hasLight = seed.hasLight) else court
        }
}

data class CourtEdit(
    val id: String, val name: String, val sport: Sport, val players: Int,
    val day: Int, val evening: Int, val weekend: Int, val active: Boolean
)

fun Court.toEdit() = CourtEdit(id, name, sport, maxPlayers,
    rate(0), rate(1), rate(2), available)

fun Court.rate(index: Int) = prices[index].hourlyRate

val Court.displayName: String
    get() = "$name · ${sport.label}" + if (sport == Sport.FOOTBALL) " $maxPlayers người" else ""

internal fun newCourt(id: String, name: String, sport: Sport, players: Int, day: Int,
                     evening: Int, weekend: Int, available: Boolean): Court {
    val surface = when (sport) {
        Sport.FOOTBALL -> "Cỏ nhân tạo"
        Sport.BADMINTON -> "Thảm PVC"
        Sport.PICKLEBALL, Sport.VOLLEYBALL -> "Sơn acrylic"
    }
    return Court(id, name, sport, surface, true, "Từ ${formatVndAmount(minOf(day, evening, weekend))}đ/giờ",
        available, players, "05:00 – 22:00", "30% giá trị booking", "15 phút",
        listOf(PriceRow("05:00 – 16:00", day),
            PriceRow("16:00 – 22:00 (T2–T6)", evening),
            PriceRow("Cuối tuần 16:00 – 22:00", weekend)))
}
