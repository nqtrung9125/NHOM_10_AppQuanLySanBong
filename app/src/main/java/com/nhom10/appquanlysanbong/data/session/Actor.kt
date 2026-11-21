package com.nhom10.appquanlysanbong.data.session

enum class Role(val label: String) {
    GUEST("Khách vãng lai"),
    CUSTOMER("Khách hàng"),
    RECEPTION("Nhân viên lễ tân"),
    MANAGER("Nhân viên quản lý"),
    ADMIN("Quản trị viên")
}

data class Actor(val id: String, val name: String, val role: Role, val phone: String = "")

object DemoAccounts {
    const val PASSWORD = "123456"
    const val OTP = "482123"
    val guest = Actor("guest", Role.GUEST.label, Role.GUEST)
    val accounts = listOf(
        Actor("trung", "Nguyễn Quốc Trung", Role.CUSTOMER, "0901234567"),
        Actor("an", "Nguyễn Minh An", Role.CUSTOMER, "0901234568"),
        Actor("reception", "Lễ tân ca trực", Role.RECEPTION, "0901234569"),
        Actor("manager", "Quản lý sân", Role.MANAGER, "0901234570"),
        Actor("admin", "Quản trị hệ thống", Role.ADMIN, "0901234571")
    )

    fun byPhone(phone: String) = accounts.find { it.phone == phone.filter(Char::isDigit) }
}
