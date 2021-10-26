package org.lineageos.loscoins.data

import org.lineageos.loscoins.model.ShopItem

interface ShopItemDataSource {
    fun getAll(): List<ShopItem>
    fun updateStore(items: List<ShopItem>)
}