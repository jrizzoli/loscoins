package org.lineageos.loscoins.db.table

import android.database.sqlite.SQLiteDatabase

interface Table {

    fun onCreate(db: SQLiteDatabase)
    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
}
