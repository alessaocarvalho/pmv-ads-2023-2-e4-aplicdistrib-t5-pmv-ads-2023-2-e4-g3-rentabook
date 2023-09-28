package br.puc.projeto.rentabook.dto

import java.time.LocalDateTime

data class AnnouncementView(
    val id: String?,
    val book: String,
    val ownerUser: String?,
    val images: List<String?>,
    val description: String,
    val createdDate: LocalDateTime,
    val isAvailable: Boolean,
    val rent: Boolean = false,
    val sale: Boolean = false,
    val trade: Boolean = false,
    val dailyValue: Long? = null,
    val saleValue: Long? = null,
    val location: String?,
)

