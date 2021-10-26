package org.lineageos.loscoins.db.data

import android.content.ContentValues
import android.content.Context
import org.lineageos.loscoins.data.RemoteStatusDataSource
import org.lineageos.loscoins.db.AppDbHelper
import org.lineageos.loscoins.db.ValueConverter
import org.lineageos.loscoins.db.column.RemoteStatusColumns
import org.lineageos.loscoins.db.table.RemoteStatusTable
import java.time.LocalDateTime

class DbRemoteStatusDataSource(
    context: Context?,
) : RemoteStatusDataSource {
    private val dbHelper = AppDbHelper.getInstance(context)

    override fun updateLastCommits() {
        update(NAME_LAST_COMMITS)
    }

    override fun updateLastShop() {
        update(NAME_LAST_SHOP)
    }

    override fun getLastCommitsUpdate(): LocalDateTime {
        return get(NAME_LAST_COMMITS)
    }

    override fun getLastShopUpdate(): LocalDateTime {
        return get(NAME_LAST_SHOP)
    }

    private fun update(name: String) {
        dbHelper.writableDatabase.use { db ->
            db.beginTransaction()
            val cv = ContentValues().apply {
                put(RemoteStatusColumns.NAME, name)
                put(
                    RemoteStatusColumns.LAST_UPDATE,
                    ValueConverter.localDateTimeToString(LocalDateTime.now())
                )
            }
            db.insert(RemoteStatusTable.NAME, null, cv)
            db.setTransactionSuccessful()
            db.endTransaction()
        }
    }

    private fun get(name: String): LocalDateTime {
        dbHelper.readableDatabase.use { db ->
            db.query(
                RemoteStatusTable.NAME, PROJECTION, SELECT_BY_NAME, arrayOf(name),
                null, null, null
            ).use { c ->
                while (c.moveToNext()) {
                    return ValueConverter.stringToLocalDateTime(c.getString(0))
                }
            }
        }
        return LocalDateTime.of(2016, 12, 24, 12, 0, 0)
    }

    private companion object {
        private val PROJECTION = arrayOf(
            RemoteStatusColumns.LAST_UPDATE,
        )

        private const val NAME_LAST_COMMITS = "last_commits"
        private const val NAME_LAST_SHOP = "last_shop"
        private const val SELECT_BY_NAME = "${RemoteStatusColumns.NAME} = ?"
    }
}