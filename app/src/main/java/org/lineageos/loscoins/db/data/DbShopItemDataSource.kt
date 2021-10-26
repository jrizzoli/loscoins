package org.lineageos.loscoins.db.data

import android.content.ContentValues
import android.content.Context
import org.lineageos.loscoins.data.ShopItemDataSource
import org.lineageos.loscoins.db.AppDbHelper
import org.lineageos.loscoins.db.ValueConverter
import org.lineageos.loscoins.db.column.ShopItemColumns
import org.lineageos.loscoins.db.table.ShopItemTable
import org.lineageos.loscoins.model.ShopItem

class DbShopItemDataSource(context: Context?) : ShopItemDataSource {
    private val dbHelper = AppDbHelper.getInstance(context)

    override fun getAll(): List<ShopItem> {
        dbHelper.readableDatabase.use { db ->
            db.query(
                ShopItemTable.NAME, PROJECTION, null, null,
                null, null, null,
            ).use { c ->
                val list = mutableListOf<ShopItem>()
                while (c.moveToNext()) {
                    list.add(
                        ShopItem(
                            name = c.getString(0),
                            price = c.getLong(1),
                            type = ValueConverter.intToShopItemType(c.getInt(2))
                        )
                    )
                }
                return list
            }
        }
    }

    override fun updateStore(items: List<ShopItem>) {
        dbHelper.writableDatabase.use { db ->
            db.beginTransaction()

            // Delete all
            db.delete(ShopItemTable.NAME, null, null)

            // Insert all
            items.forEach {
                val cv = ContentValues().apply {
                    put(ShopItemColumns.NAME, it.name)
                    put(ShopItemColumns.PRICE, it.price)
                    put(ShopItemColumns.TYPE, ValueConverter.shopItemTypeToInt(it.type))
                }
                db.insert(ShopItemTable.NAME, null, cv)
            }
            db.setTransactionSuccessful()
            db.endTransaction()
        }
    }

    private companion object {
        val PROJECTION = arrayOf(
            ShopItemColumns.NAME,
            ShopItemColumns.PRICE,
            ShopItemColumns.TYPE,
        )
    }
}