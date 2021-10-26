package org.lineageos.loscoins.db.table

import android.database.sqlite.SQLiteDatabase
import org.lineageos.loscoins.db.column.RemoteStatusColumns

object RemoteStatusTable : Table {
    const val NAME = "remote_status"

    private const val CREATE_CMD = "CREATE TABLE IF NOT EXISTS $NAME (" +
            "${RemoteStatusColumns.NAME} TEXT NOT NULL PRIMARY KEY ON CONFLICT REPLACE, " +
            "${RemoteStatusColumns.LAST_UPDATE} TEXT NOT NULL " +
            ")"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_CMD)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // empty
    }

}