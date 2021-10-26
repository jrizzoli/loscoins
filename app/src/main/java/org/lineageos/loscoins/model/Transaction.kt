package org.lineageos.loscoins.model

import java.time.LocalDateTime

data class Transaction(
    val id: Long = -1L,
    val amount: Long,
    val time: LocalDateTime,
    val type: Type,
) {
    enum class Type {
        PROMO,
        MARINATE,
        PURCHASE,
    }
}