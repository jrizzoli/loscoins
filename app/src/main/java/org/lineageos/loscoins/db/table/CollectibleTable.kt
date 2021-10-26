package org.lineageos.loscoins.db.table

import android.database.sqlite.SQLiteDatabase
import org.lineageos.loscoins.db.column.CollectibleColumns

object CollectibleTable : Table {
    const val NAME = "collectibles"

    private const val CREATE_CMD = "CREATE TABLE IF NOT EXISTS $NAME (" +
            "${CollectibleColumns.NAME} TEXT NOT NULL PRIMARY KEY, " +
            "${CollectibleColumns.TYPE} INTEGER NOT NULL, " +
            "${CollectibleColumns.OBTAINED_DATE} TEXT NOT NULL, " +
            "${CollectibleColumns.QUANTITY} INTEGER NOT NULL " +
            ")"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_CMD)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // empty
    }
}