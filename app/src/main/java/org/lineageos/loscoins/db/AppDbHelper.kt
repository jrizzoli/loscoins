package org.lineageos.loscoins.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.lineageos.loscoins.db.table.*
import org.lineageos.loscoins.util.SingletonHolder

class AppDbHelper private constructor(
    context: Context?
) : SQLiteOpenHelper(
    context,
    NAME,
    null,
    DB_VERSION,
) {

    private val tables = arrayOf(
        CollectibleTable,
        CommitTable,
        RemoteStatusTable,
        ShopItemTable,
        TransactionTable,
    )

    override fun onCreate(db: SQLiteDatabase?) {
        db?.run { tables.forEach { it.onCreate(this) } }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.run { tables.forEach { it.onUpgrade(this, oldVersion, newVersion) } }
    }

    companion object : SingletonHolder<AppDbHelper, Context?>({ AppDbHelper(it) }) {
        private const val DB_VERSION = 1
        private const val NAME = "app_db"
    }
}