package br.puc.projeto.rentabook.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document("Trade")
data class Trade (
    @Id
    val id: String? = null,
    val announcement: Announcement,
    val ownerUser: User,
    val createData: LocalDateTime = LocalDateTime.now(),
    val startDate: LocalDate,
    val tradeDate: LocalDate,
    val tradeUser: User,
    var rating: Rating? = null,
    val chat: Chat,
    val accepted: Boolean = false,

    )
