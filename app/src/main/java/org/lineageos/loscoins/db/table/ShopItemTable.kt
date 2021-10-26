package org.lineageos.loscoins.db.table

import android.database.sqlite.SQLiteDatabase
import org.lineageos.loscoins.db.column.ShopItemColumns

object ShopItemTable : Table {
    const val NAME = "shop_items"

    private const val CREATE_CMD = "CREATE TABLE IF NOT EXISTS $NAME (" +
            "${ShopItemColumns.NAME} TEXT NOT NULL PRIMARY KEY, " +
            "${ShopItemColumns.PRICE} INTEGER NOT NULL, " +
            "${ShopItemColumns.TYPE} INTEGER NOT NULL " +
            ")"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_CMD)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // empty
    }
}