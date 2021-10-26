package org.lineageos.loscoins.db.table

import android.database.sqlite.SQLiteDatabase
import org.lineageos.loscoins.db.column.TransactionColumns

object TransactionTable : Table {
    const val NAME = "transactions"

    private const val CREATE_CMD = "CREATE TABLE IF NOT EXISTS $NAME (" +
            "${TransactionColumns._ID} INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "${TransactionColumns.AMOUNT} INTEGER NOT NULL, " +
            "${TransactionColumns.TIME} TEXT NOT NULL, " +
            "${TransactionColumns.TYPE} INTEGER NOT NULL " +
            ")"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_CMD)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // empty
    }
}