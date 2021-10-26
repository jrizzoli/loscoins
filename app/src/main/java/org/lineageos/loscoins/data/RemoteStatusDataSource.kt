package org.lineageos.loscoins.data

import java.time.LocalDateTime

interface RemoteStatusDataSource {
    fun updateLastCommits()
    fun updateLastShop()
    fun getLastCommitsUpdate(): LocalDateTime
    fun getLastShopUpdate(): LocalDateTime
}