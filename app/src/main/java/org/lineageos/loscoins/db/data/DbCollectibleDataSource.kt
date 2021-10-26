package org.lineageos.loscoins.db.data

import android.content.ContentValues
import android.content.Context
import org.lineageos.loscoins.data.CollectibleDataSource
import org.lineageos.loscoins.db.AppDbHelper
import org.lineageos.loscoins.db.ValueConverter
import org.lineageos.loscoins.db.column.CollectibleColumns
import org.lineageos.loscoins.db.table.CollectibleTable
import org.lineageos.loscoins.model.Collectible

class DbCollectibleDataSource(
    context: Context?,
) : CollectibleDataSource {
    private val dbHelper = AppDbHelper.getInstance(context)

    override fun getAll(): List<Collectible> {
        dbHelper.readableDatabase.use { db ->
            db.query(
                CollectibleTable.NAME, PROJECTION, null, null,
                null, null, ORDER_BY_DATE
            ).use { c ->
                val list = mutableListOf<Collectible>()
                while (c.moveToNext()) {
                    list.add(
                        Collectible(
                            c.getString(0),
                            ValueConverter.intToShopItemType(c.getInt(1)),
                            ValueConverter.stringToLocalDate(c.getString(2)),
                            c.getInt(3),
                        )
                    )
                }
                return list
            }
        }
    }

    override fun add(collectible: Collectible) {
        val owned = quantityOwned(collectible)
        dbHelper.writableDatabase.use { db ->
            db.beginTransaction()

            if (owned == 0) {
                val cv = ContentValues().apply {
                    put(CollectibleColumns.NAME, collectible.name)
                    put(CollectibleColumns.TYPE, ValueConverter.shopItemTypeToInt(collectible.type))
                    put(
                        CollectibleColumns.OBTAINED_DATE,
                        ValueConverter.localDateToString(collectible.obtainedDate)
                    )
                    put(CollectibleColumns.QUANTITY, 1)
                }
                db.insert(CollectibleTable.NAME, null, cv)
            } else {
                val cv = ContentValues().apply {
                    put(CollectibleColumns.QUANTITY, owned + 1)
                }
                db.update(CollectibleTable.NAME, cv, SELECT_BY_NAME, arrayOf(collectible.name))
            }

            db.setTransactionSuccessful()
            db.endTransaction()
        }
    }

    private fun quantityOwned(collectible: Collectible): Int {
        dbHelper.readableDatabase.use { db ->
            db.query(
                CollectibleTable.NAME, PROJECTION_COUNT, SELECT_BY_NAME, arrayOf(collectible.name),
                null, null, ORDER_BY_DATE
            ).use { c ->
                return if (c.moveToFirst())
                    c.getInt(0)
                else
                    0
            }
        }
    }

    private companion object {
        val PROJECTION = arrayOf(
            CollectibleColumns.NAME,
            CollectibleColumns.TYPE,
            CollectibleColumns.OBTAINED_DATE,
            CollectibleColumns.QUANTITY,
        )
        val PROJECTION_COUNT = arrayOf(
            CollectibleColumns.QUANTITY,
        )

        const val SELECT_BY_NAME = "${CollectibleColumns.NAME} =  ?"
        const val ORDER_BY_DATE = "${CollectibleColumns.OBTAINED_DATE} DESC"
    }
}