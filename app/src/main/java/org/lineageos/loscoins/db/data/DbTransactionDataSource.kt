package org.lineageos.loscoins.db.data

import android.content.ContentValues
import android.content.Context
import org.lineageos.loscoins.data.TransactionDataSource
import org.lineageos.loscoins.db.AppDbHelper
import org.lineageos.loscoins.db.ValueConverter
import org.lineageos.loscoins.db.column.CommitColumns
import org.lineageos.loscoins.db.column.TransactionColumns
import org.lineageos.loscoins.db.table.CommitTable
import org.lineageos.loscoins.db.table.TransactionTable
import org.lineageos.loscoins.model.TrackedCommit
import org.lineageos.loscoins.model.Transaction

class DbTransactionDataSource(
    context: Context?
) : TransactionDataSource {
    private val dbHelper = AppDbHelper.getInstance(context)

    override fun getAll(): List<Transaction> {
        dbHelper.readableDatabase.use { db ->
            db.query(
                TransactionTable.NAME, PROJECTION, null, null,
                null, null, ORDER_BY_TIME
            ).use { c ->
                val list = mutableListOf<Transaction>()
                while (c.moveToNext()) {
                    list.add(
                        Transaction(
                            id = c.getLong(0),
                            amount = c.getLong(1),
                            time = ValueConverter.stringToLocalDateTime(c.getString(2)),
                            type = ValueConverter.intToTransactionType(c.getInt(3))
                        )
                    )
                }
                return list
            }
        }
    }

    override fun add(transaction: Transaction) {
        dbHelper.writableDatabase.use { db ->
            db.beginTransaction()

            val cv = ContentValues().apply {
                put(TransactionColumns.AMOUNT, transaction.amount)
                put(TransactionColumns.TIME, ValueConverter.localDateTimeToString(transaction.time))
                put(TransactionColumns.TYPE, ValueConverter.transactionTypeToInt(transaction.type))
            }

            db.insert(TransactionTable.NAME, null, cv)
            db.setTransactionSuccessful()
            db.endTransaction()
        }
    }

    override fun reset() {
        dbHelper.writableDatabase.use { db ->
            db.beginTransaction()
            db.delete(TransactionTable.NAME, null, null)
            db.setTransactionSuccessful()
            db.endTransaction()
        }
    }

    override fun currentBalance(): Long {
        dbHelper.readableDatabase.use { db ->
            db.query(
                TransactionTable.NAME, arrayOf(TransactionColumns.AMOUNT), null, null,
                null, null, null
            ).use { c ->
                var balance = 0L
                while (c.moveToNext()) {
                    balance += c.getLong(0)
                }
                return balance
            }
        }
    }


    private companion object {
        val PROJECTION = arrayOf(
            TransactionColumns._ID,
            TransactionColumns.AMOUNT,
            TransactionColumns.TIME,
            TransactionColumns.TYPE,
        )
        const val ORDER_BY_TIME = "${TransactionColumns.TIME} DESC"
    }
}