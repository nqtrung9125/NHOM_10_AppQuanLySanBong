package com.nhom10.appquanlysanbong.data.session

import android.content.Context
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.data.local.LocalRecords
import com.nhom10.appquanlysanbong.data.local.json
import java.security.MessageDigest

/** Tài khoản demo chỉ tồn tại trên thiết bị. Vai trò nội bộ được cấp sẵn trong DemoAccounts. */
class DemoAccountRepository(context: Context) {
    private val records = LocalRecords(context)

    private val accountEdits = context.getSharedPreferences("demo_account_edits", Context.MODE_PRIVATE)

    fun all(): List<Actor> = (DemoAccounts.accounts + registered().map { it.first }).map(::withEdits)

    fun find(phone: String): Actor? {
        val key = normalize(phone)
        val actor = DemoAccounts.byPhone(key) ?: registered().firstOrNull { it.first.phone == key }?.first
        return actor?.let(::withEdits)
    }

    fun accepts(phone: String, password: String): Boolean {
        val key = normalize(phone)
        val savedProof = accountEdits.getString("$key.passwordProof", null)
        val proof = savedProof ?: if (DemoAccounts.byPhone(key) != null) {
            registrationProof(key, DemoAccounts.PASSWORD)
        } else {
            registered().firstOrNull { it.first.phone == key }?.second ?: return false
        }
        return proof == registrationProof(key, password)
    }

    internal fun updateProfile(phone: String, name: String, skillLevel: Int): Boolean {
        val actor = find(phone) ?: return false
        if (actor.role != Role.CUSTOMER || name.isBlank() || skillLevel !in DemoData.LEVELS.indices) return false
        accountEdits.edit()
            .putString("${actor.phone}.name", name.trim())
            .putInt("${actor.phone}.level", skillLevel)
            .apply()
        return true
    }

    internal fun changePassword(phone: String, current: String, replacement: String): Boolean = synchronized(passwordLock) {
        val actor = find(phone) ?: return@synchronized false
        if (actor.role != Role.CUSTOMER || replacement.length < 6 || !accepts(actor.phone, current)) return@synchronized false
        accountEdits.edit()
            .putString("${actor.phone}.passwordProof", registrationProof(actor.phone, replacement))
            .apply()
        true
    }

    private fun withEdits(actor: Actor) = actor.copy(
        name = accountEdits.getString("${actor.phone}.name", actor.name) ?: actor.name,
        skillLevel = accountEdits.getInt("${actor.phone}.level", actor.skillLevel)
    )

    internal fun register(phone: String, name: String, proof: String): Boolean {
        val key = normalize(phone)
        if (key.length !in 9..11 || name.isBlank() || proof.isBlank() || find(key) != null) return false
        val account = Actor(key, name.trim(), Role.CUSTOMER, key)
        records.write("demo_accounts", registered() + (account to proof)) { (actor, digest) ->
            json("phone" to actor.phone, "name" to actor.name, "proof" to digest)
        }
        return true
    }

    private fun registered(): List<Pair<Actor, String>> = records.read("demo_accounts") {
        val phone = it.getString("phone")
        Actor(phone, it.getString("name"), Role.CUSTOMER, phone) to it.getString("proof")
    }

    companion object {
        private val passwordLock = Any()

        fun normalize(phone: String) = phone.filter(Char::isDigit)

        // Không lưu mật khẩu nhập trong form demo vào trạng thái Fragment hay kho cục bộ.
        fun registrationProof(phone: String, password: String): String =
            MessageDigest.getInstance("SHA-256").digest("${normalize(phone)}:$password".toByteArray())
                .joinToString("") { "%02x".format(it) }
    }
}
