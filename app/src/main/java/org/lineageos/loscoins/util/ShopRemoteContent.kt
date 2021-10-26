package org.lineageos.loscoins.util

import android.util.Log
import org.lineageos.loscoins.data.RemoteStatusDataSource
import org.lineageos.loscoins.data.ShopItemDataSource
import org.lineageos.loscoins.model.ShopItem
import java.net.URL
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object ShopRemoteContent {
    private const val TAG = "ShopRemoteContent"

    fun getShopItems(
        dataSource: ShopItemDataSource,
        remoteStatusDataSource: RemoteStatusDataSource,
        storeUrl: String,
        forceRefresh: Boolean = false
    ): List<ShopItem> {
        return if (forceRefresh ||
            remoteStatusDataSource.getLastShopUpdate()
                .until(LocalDateTime.now(), ChronoUnit.HOURS) > 24
        ) {
            Log.d(TAG, "Fetching shop data")
            val list = fetchShopItems(storeUrl)
            dataSource.updateStore(list)
            remoteStatusDataSource.updateLastShop()
            list
        } else {
            Log.d(TAG, "Using cached shop data")
            dataSource.getAll()
        }
    }

    private fun fetchShopItems(storeUrl: String): List<ShopItem> {
        val json = getJsonArray(URL(storeUrl))
        Log.d(TAG, json.toString(2))

        val list = mutableListOf<ShopItem>()
        var i = 0
        while (i < json.length()) {
            val obj = json.getJSONObject(i++)
            list.add(
                ShopItem(
                    name = obj.getString("name"),
                    price = obj.getLong("price"),
                    type = parseType(obj.getString("type"))
                )
            )
        }
        return list
    }

    private fun parseType(str: String?): ShopItem.Type {
        return when (str) {
            "bug" -> ShopItem.Type.BUG
            "device" -> ShopItem.Type.DEVICE
            "eta" -> ShopItem.Type.ETA
            "feature" -> ShopItem.Type.FEATURE
            "key" -> ShopItem.Type.KEY
            "money" -> ShopItem.Type.MONEY
            "permission" -> ShopItem.Type.PERMISSION
            "promo" -> ShopItem.Type.PROMO
            "rom" -> ShopItem.Type.ROM
            "yacht" -> ShopItem.Type.YACHT
            else -> ShopItem.Type.UNDEFINED
        }
    }
}