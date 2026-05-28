package com.homepantry.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.homepantry.db.PantryDatabase
import com.homepantry.domain.model.FoodCategory
import com.homepantry.domain.model.InventoryItem
import com.homepantry.domain.model.StorageLocation
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InventoryRepositoryTest {

    private lateinit var database: PantryDatabase
    private lateinit var repository: InventoryRepository

    @BeforeTest
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PantryDatabase.Schema.create(driver)
        database = PantryDatabase(driver)
        repository = InventoryRepository(database)
    }

    @Test
    fun testAddAndGetAll() {
        // Arrange
        val item = InventoryItem(
            canonicalName = "olive_oil",
            displayName = "Extra Virgin Olive Oil",
            category = FoodCategory.SPICE,
            location = StorageLocation.SPICE,
            quantity = 1.0,
            unit = "bottle"
        )

        // Act
        repository.add(item)
        val allItems = repository.getAll()

        // Assert
        assertEquals(1, allItems.size)
        val retrieved = allItems[0]
        assertEquals("olive_oil", retrieved.canonicalName)
        assertEquals("Extra Virgin Olive Oil", retrieved.displayName)
        assertEquals(FoodCategory.SPICE, retrieved.category)
        assertEquals(StorageLocation.SPICE, retrieved.location)
        assertEquals(1.0, retrieved.quantity)
        assertEquals("bottle", retrieved.unit)
    }

    @Test
    fun testUpdateItem() {
        // Arrange
        val item = InventoryItem(
            canonicalName = "chicken",
            displayName = "Chicken",
            category = FoodCategory.PROTEIN,
            location = StorageLocation.FRIDGE,
            quantity = 1.0,
            unit = "lb"
        )
        repository.add(item)
        val saved = repository.getAll().first()

        // Act
        val updated = saved.copy(quantity = 2.5, displayName = "Organic Chicken")
        repository.update(updated)

        // Assert
        val retrieved = repository.getById(saved.id)
        assertNotNull(retrieved)
        assertEquals(2.5, retrieved.quantity)
        assertEquals("Organic Chicken", retrieved.displayName)
    }

    @Test
    fun testDeleteItem() {
        // Arrange
        val item = InventoryItem(
            canonicalName = "garlic",
            displayName = "Garlic",
            category = FoodCategory.SPICE,
            location = StorageLocation.SPICE,
            quantity = 3.0,
            unit = "clove"
        )
        repository.add(item)
        val saved = repository.getAll().first()

        // Act
        repository.delete(saved.id)

        // Assert
        assertNull(repository.getById(saved.id))
        assertTrue(repository.getAll().isEmpty())
    }
}
