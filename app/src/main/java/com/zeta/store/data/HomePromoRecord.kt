package com.zeta.store.data

data class HomePromoRecord(
    val id: String,
    val eyebrow: String,
    val title: String,
    val detail: String,
    val colorHexes: List<String>,
    val imageKey: String,
    val imageUrl: String,
    val displayOrder: Int,
    val isActive: Boolean,
)
