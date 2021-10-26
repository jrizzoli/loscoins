package org.lineageos.loscoins.data

import org.lineageos.loscoins.model.Collectible

interface CollectibleDataSource {
    fun getAll(): List<Collectible>
    fun add(collectible: Collectible)
}