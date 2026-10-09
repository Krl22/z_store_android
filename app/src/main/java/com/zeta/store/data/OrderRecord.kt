package com.zeta.store.data

data class OrderItemInput(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Int,
    val imageUrl: String,
)

data class AddressRecord(
    val id: String,
    val userId: String,
    val label: String,
    val recipientName: String,
    val phone: String,
    val line1: String,
    val line2: String,
    val district: String,
    val city: String,
    val country: String,
    val reference: String,
    val isDefault: Boolean,
)

data class OrderItemRecord(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Int,
    val imageUrl: String,
) {
    val lineTotal: Int
        get() = quantity * unitPrice
}

data class OrderRecord(
    val id: String,
    val userId: String,
    val customerEmail: String,
    val status: String,
    val paymentStatus: String,
    val subtotal: Int,
    val total: Int,
    val addressId: String,
    val deliveryMethod: String,
    val deliveryStatus: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddressSnapshot: String,
    val createdAt: String,
    val items: List<OrderItemRecord>,
)

data class AdminNotificationRecord(
    val id: String,
    val orderId: String,
    /** order | customer | stock */
    val kind: String,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val createdAt: String,
)
