package org.lineageos.loscoins.db

import org.lineageos.loscoins.model.ShopItem
import org.lineageos.loscoins.model.Transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ValueConverter {

    fun stringToLocalDate(value: String): LocalDate {
        return LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(value))
    }

    fun localDateToString(value: LocalDate): String {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(value)
    }

    fun stringToLocalDateTime(value: String): LocalDateTime {
        return LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(value))
    }

    fun localDateTimeToString(value: LocalDateTime): String {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value)
    }

    fun intToShopItemType(value: Int): ShopItem.Type {
        return when (value) {
            0 -> ShopItem.Type.BUG
            1 -> ShopItem.Type.DEVICE
            2 -> ShopItem.Type.ETA
            3 -> ShopItem.Type.FEATURE
            4 -> ShopItem.Type.KEY
            5 -> ShopItem.Type.MONEY
            6 -> ShopItem.Type.PERMISSION
            7 -> ShopItem.Type.PROMO
            8 -> ShopItem.Type.ROM
            9 -> ShopItem.Type.UNDEFINED
            10 -> ShopItem.Type.YACHT
            else -> throw NotImplementedError()
        }
    }

    fun shopItemTypeToInt(value: ShopItem.Type): Int {
        return when (value) {
            ShopItem.Type.BUG -> 0
            ShopItem.Type.DEVICE -> 1
            ShopItem.Type.ETA -> 2
            ShopItem.Type.FEATURE -> 3
            ShopItem.Type.KEY -> 4
            ShopItem.Type.MONEY -> 5
            ShopItem.Type.PERMISSION -> 6
            ShopItem.Type.PROMO -> 7
            ShopItem.Type.ROM -> 8
            ShopItem.Type.UNDEFINED -> 9
            ShopItem.Type.YACHT -> 10
        }
    }

    fun intToTransactionType(value: Int): Transaction.Type {
        return when (value) {
            0 -> Transaction.Type.PROMO
            1 -> Transaction.Type.MARINATE
            2 -> Transaction.Type.PURCHASE
            else -> throw NotImplementedError()
        }
    }

    fun transactionTypeToInt(value: Transaction.Type): Int {
        return when (value) {
            Transaction.Type.PROMO -> 0
            Transaction.Type.MARINATE -> 1
            Transaction.Type.PURCHASE -> 2
        }
    }
}