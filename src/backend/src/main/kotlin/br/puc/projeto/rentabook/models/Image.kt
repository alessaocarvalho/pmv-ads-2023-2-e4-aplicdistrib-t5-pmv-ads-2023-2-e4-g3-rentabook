package br.puc.projeto.rentabook.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("images")
data class Image(
    @Id
    val id: String? = null,
    val path: String,
)