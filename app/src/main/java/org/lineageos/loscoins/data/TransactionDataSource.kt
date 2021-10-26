package org.lineageos.loscoins.data

import org.lineageos.loscoins.model.Transaction

interface TransactionDataSource {
    fun getAll(): List<Transaction>
    fun add(transaction: Transaction)
    fun reset()
    fun currentBalance(): Long
}