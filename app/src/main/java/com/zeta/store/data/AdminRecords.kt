package com.zeta.store.data

/** Fila de admin_list_customers: perfil, acceso y actividad de compra. */
data class AdminCustomerRecord(
    val id: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val role: String,
    val createdAt: String,
    val lastSignInAt: String,
    val emailConfirmed: Boolean,
    val providers: List<String>,
    val ordersCount: Int,
    val pendingOrdersCount: Int,
    val totalPaid: Int,
    val lastOrderAt: String,
    val addressesCount: Int,
    val cartUnits: Int,
) {
    val isAdmin: Boolean
        get() = role == "admin"
}

/** Fila única de store_settings. */
data class StoreSettingsRecord(
    val whatsappNumber: String,
    val adRotationSeconds: Int,
    val lowStockThreshold: Int,
)

/** Qué avisos recibe un admin en todos sus dispositivos (admin_notification_preferences). */
data class AdminNotificationPreferences(
    val enabled: Boolean = true,
    val newOrders: Boolean = true,
    val newCustomers: Boolean = true,
    val lowStock: Boolean = true,
)
