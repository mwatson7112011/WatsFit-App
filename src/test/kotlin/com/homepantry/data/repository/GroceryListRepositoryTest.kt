package com.homepantry.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.homepantry.db.PantryDatabase
import com.homepantry.domain.model.GroceryListItem
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GroceryListRepositoryTest {

    private lateinit var database: PantryDatabase
    private lateinit var repository: GroceryListRepository

    @BeforeTest
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PantryDatabase.Schema.create(driver)
        database = PantryDatabase(driver)
        repository = GroceryListRepository(database)
    }

    @Test
    fun testAddAndGetAll() {
        // Arrange
        val item = GroceryListItem(
            canonicalName = "milk",
            displayName = "Almond Milk",
            quantity = 1.0,
            unit = "carton",
            addedFrom = "manual"
        )

        // Act
        repository.add(item)
        val allItems = repository.getAll()

        // Assert
        assertEquals(1, allItems.size)
        val retrieved = allItems[0]
        assertEquals("milk", retrieved.canonicalName)
        assertEquals("Almond Milk", retrieved.displayName)
        assertEquals(1.0, retrieved.quantity)
        assertEquals("carton", retrieved.unit)
        assertEquals("manual", retrieved.addedFrom)
        assertEquals(false, retrieved.checked)
    }

    @Test
    fun testToggleChecked() {
        // Arrange
        val item = GroceryListItem(
            canonicalName = "spinach",
            displayName = "Baby Spinach",
            quantity = 1.0,
            unit = "bag"
        )
        repository.add(item)
        val saved = repository.getAll().first()

        // Act
        repository.toggleChecked(saved.id)

        // Assert
        val updated = repository.getAll().first()
        assertEquals(true, updated.checked)
    }

    @Test
    fun testDeleteChecked() {
        // Arrange
        repository.add(GroceryListItem(canonicalName = "apples", displayName = "Apples", quantity = 3.0, unit = "pieces"))
        repository.add(GroceryListItem(canonicalName = "bananas", displayName = "Bananas", quantity = 5.0, unit = "pieces"))
        val items = repository.getAll()
        val apples = items.first { it.canonicalName == "apples" }

        // Check off apples
        repository.toggleChecked(apples.id)

        // Act
        repository.deleteChecked()

        // Assert
        val remaining = repository.getAll()
        assertEquals(1, remaining.size)
        assertEquals("bananas", remaining[0].canonicalName)
    }
}
