package com.homepantry.domain.service

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.homepantry.db.PantryDatabase
import com.homepantry.data.repository.GroceryListRepository
import com.homepantry.data.repository.InventoryRepository
import com.homepantry.domain.model.Cuisine
import com.homepantry.domain.model.FoodCategory
import com.homepantry.domain.model.InventoryItem
import com.homepantry.domain.model.Recipe
import com.homepantry.domain.model.RecipeIngredient
import com.homepantry.domain.model.StorageLocation
import com.homepantry.domain.model.TextureCategory
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InventoryManagerTest {

    private lateinit var database: PantryDatabase
    private lateinit var inventoryRepository: InventoryRepository
    private lateinit var groceryListRepository: GroceryListRepository
    private lateinit var inventoryManager: InventoryManager

    @BeforeTest
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PantryDatabase.Schema.create(driver)
        database = PantryDatabase(driver)
        inventoryRepository = InventoryRepository(database)
        groceryListRepository = GroceryListRepository(database)
        inventoryManager = InventoryManager(inventoryRepository, groceryListRepository)
    }

    @Test
    fun testCookRecipeDeductsInventory() {
        // Arrange
        val item = InventoryItem(
            canonicalName = "chicken",
            displayName = "Chicken Breast",
            category = FoodCategory.PROTEIN,
            location = StorageLocation.FRIDGE,
            quantity = 2.0,
            unit = "lb"
        )
        inventoryRepository.add(item)
        val savedItem = inventoryRepository.getAll().first()

        val recipe = Recipe(
            title = "Simple Chicken",
            category = "dinner",
            cuisine = Cuisine.MEDITERRANEAN,
            texture = TextureCategory.TENDER,
            tags = listOf("simple"),
            ingredients = listOf(RecipeIngredient("chicken", "Chicken", 0.5, "lb")),
            instructions = "Cook it"
        )

        // Act
        inventoryManager.cookRecipe(recipe, addMissingToGroceryList = false)

        // Assert
        val remainingItems = inventoryRepository.getAll()
        assertEquals(1, remainingItems.size)
        assertEquals(1.5, remainingItems[0].quantity)
    }

    @Test
    fun testCookRecipeDeletesIfQuantityZeroOrLess() {
        // Arrange
        val item = InventoryItem(
            canonicalName = "chicken",
            displayName = "Chicken Breast",
            category = FoodCategory.PROTEIN,
            location = StorageLocation.FRIDGE,
            quantity = 1.0,
            unit = "lb"
        )
        inventoryRepository.add(item)

        val recipe = Recipe(
            title = "Simple Chicken",
            category = "dinner",
            cuisine = Cuisine.MEDITERRANEAN,
            texture = TextureCategory.TENDER,
            tags = listOf("simple"),
            ingredients = listOf(RecipeIngredient("chicken", "Chicken", 1.0, "lb")),
            instructions = "Cook it"
        )

        // Act
        inventoryManager.cookRecipe(recipe, addMissingToGroceryList = false)

        // Assert
        val remainingItems = inventoryRepository.getAll()
        assertTrue(remainingItems.isEmpty())
    }

    @Test
    fun testCookRecipeAddsMissingToGroceryList() {
        // Arrange
        val recipe = Recipe(
            title = "Simple Chicken",
            category = "dinner",
            cuisine = Cuisine.MEDITERRANEAN,
            texture = TextureCategory.TENDER,
            tags = listOf("simple"),
            ingredients = listOf(
                RecipeIngredient("chicken", "Chicken", 1.0, "lb"),
                RecipeIngredient("salt", "Salt", 0.1, "tsp", optional = true) // salt is optional
            ),
            instructions = "Cook it"
        )

        // Act
        inventoryManager.cookRecipe(recipe, addMissingToGroceryList = true)

        // Assert
        val groceryItems = groceryListRepository.getAll()
        assertEquals(1, groceryItems.size)
        assertEquals("chicken", groceryItems[0].canonicalName)
        assertEquals("Simple Chicken", groceryItems[0].addedFrom)
    }
}
