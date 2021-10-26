package org.lineageos.loscoins.model

data class ShopItem(
    val name: String,
    val price: Long,
    val type: Type,
) {

    enum class Type {
        BUG,
        DEVICE,
        ETA,
        FEATURE,
        KEY,
        MONEY,
        PERMISSION,
        PROMO,
        ROM,
        UNDEFINED,
        YACHT,
    }
}