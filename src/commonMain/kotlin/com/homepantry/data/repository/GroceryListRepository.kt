package com.homepantry.data.repository

import com.homepantry.db.PantryDatabase
import com.homepantry.domain.model.GroceryListItem
import kotlinx.datetime.Clock

class GroceryListRepository(private val database: PantryDatabase) {

    private val queries = database.groceryListItemQueries

    fun getAll(): List<GroceryListItem> =
        queries.selectAll().executeAsList().map { it.toDomain() }

    fun getUnchecked(): List<GroceryListItem> =
        queries.selectUnchecked().executeAsList().map { it.toDomain() }

    fun toggleChecked(id: Long) {
        queries.toggleChecked(id)
    }

    fun add(item: GroceryListItem) {
        val now = Clock.System.now().toString()
        queries.insert(
            canonicalName = item.canonicalName,
            displayName = item.displayName,
            quantity = item.quantity,
            unit = item.unit,
            addedFrom = item.addedFrom,
            createdAt = now
        )
    }

    fun delete(id: Long) {
        queries.deleteById(id)
    }

    fun deleteChecked() {
        queries.deleteChecked()
    }

    fun getUncheckedCount(): Long =
        queries.countUnchecked().executeAsOne()

    private fun com.homepantry.db.GroceryListItem.toDomain(): GroceryListItem =
        GroceryListItem(
            id = id,
            canonicalName = canonicalName,
            displayName = displayName,
            quantity = quantity,
            unit = unit,
            checked = checked != 0L,
            addedFrom = addedFrom,
            createdAt = createdAt
        )
}
