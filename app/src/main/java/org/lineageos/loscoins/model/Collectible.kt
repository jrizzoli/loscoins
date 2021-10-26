package org.lineageos.loscoins.model

import java.time.LocalDate

data class Collectible(
    val name: String,
    val type: ShopItem.Type,
    val obtainedDate: LocalDate,
    val quantity: Int,
)