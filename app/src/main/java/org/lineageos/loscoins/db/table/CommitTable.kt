package org.lineageos.loscoins.db.table

import android.database.sqlite.SQLiteDatabase
import org.lineageos.loscoins.db.column.CommitColumns

object CommitTable : Table {
    const val NAME = "commits"

    private const val CREATE_CMD = "CREATE TABLE IF NOT EXISTS $NAME (" +
            "${CommitColumns._ID} INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "${CommitColumns.BRANCH} TEXT NOT NULL, " +
            "${CommitColumns.SUBJECT} TEXT NOT NULL, " +
            "${CommitColumns.OWNER} TEXT NOT NULL, " +
            "${CommitColumns.CREATION_DATE} TEXT NOT NULL, " +
            "${CommitColumns.CLOSE_DATE} TEXT " +
            ")"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_CMD)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // empty
    }
}