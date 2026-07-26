package com.zeta.store.data

data class ProductRecord(
    val id: String,
    val name: String,
    val latin: String,
    val format: String,
    val category: String,
    val price: Int,
    val offerPrice: Int? = null,
    val isFeaturedOffer: Boolean = false,
    val tag: String,
    val rating: String,
    val colorHex: String,
    val accentHex: String,
    val benefits: List<String>,
    val stock: Int,
    val imageKeys: List<String>,
    val imageUrls: List<String>,
    val isActive: Boolean,
    val displayOrder: Int,
)
