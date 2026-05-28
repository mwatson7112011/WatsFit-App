package com.homepantry.data.repository

import com.homepantry.db.PantryDatabase
import com.homepantry.domain.model.FoodCategory
import com.homepantry.domain.model.InventoryItem
import com.homepantry.domain.model.StorageLocation
import kotlinx.datetime.Clock

class InventoryRepository(private val database: PantryDatabase) {

    private val queries = database.inventoryItemQueries

    fun getAll(): List<InventoryItem> =
        queries.selectAll().executeAsList().map { it.toDomain() }

    fun getByLocation(location: String): List<InventoryItem> =
        queries.selectByLocation(location).executeAsList().map { it.toDomain() }

    fun getById(id: Long): InventoryItem? =
        queries.selectById(id).executeAsOneOrNull()?.toDomain()

    fun search(query: String): List<InventoryItem> =
        queries.searchByName(query, query).executeAsList().map { it.toDomain() }

    fun getExpiringSoon(beforeDate: String): List<InventoryItem> =
        queries.selectExpiringSoon(beforeDate).executeAsList().map { it.toDomain() }

    fun add(item: InventoryItem) {
        val now = Clock.System.now().toString()
        queries.insert(
            canonicalName = item.canonicalName,
            displayName = item.displayName,
            category = item.category.name,
            location = item.location.name,
            quantity = item.quantity,
            unit = item.unit,
            expirationDate = item.expirationDate,
            notes = item.notes,
            createdAt = now,
            updatedAt = now
        )
    }

    fun update(item: InventoryItem) {
        val now = Clock.System.now().toString()
        queries.update(
            canonicalName = item.canonicalName,
            displayName = item.displayName,
            category = item.category.name,
            location = item.location.name,
            quantity = item.quantity,
            unit = item.unit,
            expirationDate = item.expirationDate,
            notes = item.notes,
            updatedAt = now,
            id = item.id
        )
    }

    fun updateQuantity(id: Long, newQuantity: Double) {
        val now = Clock.System.now().toString()
        queries.updateQuantity(
            quantity = newQuantity,
            updatedAt = now,
            id = id
        )
    }

    fun delete(id: Long) {
        queries.deleteById(id)
    }

    fun getCountByLocation(): Map<String, Long> =
        queries.countByLocation().executeAsList().associate { row ->
            row.location to row.count
        }

    private fun com.homepantry.db.InventoryItem.toDomain(): InventoryItem =
        InventoryItem(
            id = id,
            canonicalName = canonicalName,
            displayName = displayName,
            category = FoodCategory.fromString(category),
            location = StorageLocation.fromString(location),
            quantity = quantity,
            unit = unit,
            expirationDate = expirationDate,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun com.homepantry.db.SelectExpiringSoon.toDomain(): InventoryItem =
        InventoryItem(
            id = id,
            canonicalName = canonicalName,
            displayName = displayName,
            category = FoodCategory.fromString(category),
            location = StorageLocation.fromString(location),
            quantity = quantity,
            unit = unit,
            expirationDate = expirationDate,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}
