package com.zeta.store.data

data class ProfileRecord(
    val id: String,
    val email: String,
    val role: String,
    val fullName: String = "",
    val phone: String = "",
    val defaultAddressId: String = "",
) {
    val isAdmin: Boolean
        get() = role == "admin"
}
