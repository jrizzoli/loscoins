package org.lineageos.loscoins.model

import java.time.LocalDate

data class Commit(
    val id: Int,
    val branch: String,
    val subject: String,
    val owner: String,
    val creationDate: LocalDate,
    val closeDate: LocalDate?,
)
