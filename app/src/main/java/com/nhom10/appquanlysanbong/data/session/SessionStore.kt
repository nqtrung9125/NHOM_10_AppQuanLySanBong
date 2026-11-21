package com.nhom10.appquanlysanbong.data.session

import android.content.Context

/** Phiên demo lưu cục bộ. Khi tích hợp backend, vai trò phải lấy từ phiên do máy chủ cấp. */
class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("actor_session", Context.MODE_PRIVATE)
    private val identity = DemoAccountRepository(context)

    val current: Actor
        get() {
            val phone = prefs.getString("phone", null) ?: return DemoAccounts.guest
            return identity.find(phone) ?: DemoAccounts.guest
        }

    fun signIn(phone: String): Boolean {
        val account = identity.find(phone) ?: return false
        prefs.edit().putString("phone", account.phone).apply()
        return true
    }

    fun registerAndSignIn(phone: String, name: String, proof: String): Boolean =
        identity.register(phone, name, proof) && signIn(phone)

    fun signOut() = prefs.edit().clear().apply()
}
