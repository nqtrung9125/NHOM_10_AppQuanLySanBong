package com.nhom10.appquanlysanbong.data.session

import android.content.Context
import com.nhom10.appquanlysanbong.data.local.LocalRecords
import com.nhom10.appquanlysanbong.data.local.json
import java.security.MessageDigest

/** Tài khoản demo chỉ tồn tại trên thiết bị. Vai trò nội bộ được cấp sẵn trong DemoAccounts. */
class DemoAccountRepository(context: Context) {
    private val records = LocalRecords(context)

    fun all(): List<Actor> = DemoAccounts.accounts + registered().map { it.first }

    fun find(phone: String): Actor? = all().find { it.phone == normalize(phone) }

    fun accepts(phone: String, password: String): Boolean {
        val demo = DemoAccounts.byPhone(phone)
        if (demo != null) return password == DemoAccounts.PASSWORD
        return registered().any { (actor, proof) ->
            actor.phone == normalize(phone) && proof == registrationProof(phone, password)
        }
    }

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
        fun normalize(phone: String) = phone.filter(Char::isDigit)

        // Không lưu mật khẩu nhập trong form demo vào trạng thái Fragment hay kho cục bộ.
        fun registrationProof(phone: String, password: String): String =
            MessageDigest.getInstance("SHA-256").digest("${normalize(phone)}:$password".toByteArray())
                .joinToString("") { "%02x".format(it) }
    }
}
