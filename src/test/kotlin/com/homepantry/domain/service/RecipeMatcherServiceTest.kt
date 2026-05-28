package com.homepantry.domain.service

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.homepantry.db.PantryDatabase
import com.homepantry.data.repository.InventoryRepository
import com.homepantry.data.repository.RecipeRepository
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

class RecipeMatcherServiceTest {

    private lateinit var database: PantryDatabase
    private lateinit var inventoryRepository: InventoryRepository
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var recipeMatcherService: RecipeMatcherService

    @BeforeTest
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PantryDatabase.Schema.create(driver)
        database = PantryDatabase(driver)
        inventoryRepository = InventoryRepository(database)
        recipeRepository = RecipeRepository(database)
        recipeMatcherService = RecipeMatcherService(inventoryRepository, recipeRepository)
    }

    @Test
    fun testMatchRecipesFilterByTexture() {
        // Arrange
        val recipeSoft = Recipe(
            title = "Soft Soup",
            category = "lunch",
            cuisine = Cuisine.MEDITERRANEAN,
            texture = TextureCategory.SOFT,
            tags = listOf("soup"),
            ingredients = listOf(RecipeIngredient("water", "Water", 1.0, "cup")),
            instructions = "Mix it"
        )
        val recipeCrunchy = Recipe(
            title = "Crunchy Salad",
            category = "lunch",
            cuisine = Cuisine.MEDITERRANEAN,
            texture = TextureCategory.CRUNCHY,
            tags = listOf("salad"),
            ingredients = listOf(RecipeIngredient("carrots", "Carrots", 1.0, "cup")),
            instructions = "Chop and mix"
        )
        recipeRepository.insert(recipeSoft)
        recipeRepository.insert(recipeCrunchy)

        // Act
        val matches = recipeMatcherService.matchRecipes(textureFilter = setOf(TextureCategory.SOFT))

        // Assert
        assertEquals(1, matches.size)
        assertEquals("Soft Soup", matches[0].recipe.title)
    }

    @Test
    fun testMatchRecipesCuisineFilter() {
        // Arrange
        val recipeMed = Recipe(
            title = "Med Greek Salad",
            category = "lunch",
            cuisine = Cuisine.MEDITERRANEAN,
            texture = TextureCategory.TENDER,
            tags = listOf("salad"),
            ingredients = listOf(RecipeIngredient("tomato", "Tomato", 1.0, "piece")),
            instructions = "Chop"
        )
        val recipeThai = Recipe(
            title = "Thai Tom Kha",
            category = "lunch",
            cuisine = Cuisine.THAI,
            texture = TextureCategory.SOFT,
            tags = listOf("soup"),
            ingredients = listOf(RecipeIngredient("coconut_milk", "Coconut Milk", 1.0, "cup")),
            instructions = "Boil"
        )
        recipeRepository.insert(recipeMed)
        recipeRepository.insert(recipeThai)

        // Act
        val matchesMed = recipeMatcherService.matchRecipes(textureFilter = emptySet(), cuisineFilter = Cuisine.MEDITERRANEAN)
        val matchesThai = recipeMatcherService.matchRecipes(textureFilter = emptySet(), cuisineFilter = Cuisine.THAI)

        // Assert
        assertEquals(1, matchesMed.size)
        assertEquals("Med Greek Salad", matchesMed[0].recipe.title)
        assertEquals(1, matchesThai.size)
        assertEquals("Thai Tom Kha", matchesThai[0].recipe.title)
    }

    @Test
    fun testRecipeMatchingScores() {
        // Arrange
        val recipe = Recipe(
            title = "Chicken Curry",
            category = "dinner",
            cuisine = Cuisine.THAI,
            texture = TextureCategory.TENDER,
            tags = listOf("curry"),
            ingredients = listOf(
                RecipeIngredient("chicken", "Chicken", 1.0, "lb"),
                RecipeIngredient("coconut_milk", "Coconut Milk", 1.0, "can"),
                RecipeIngredient("curry_paste", "Curry Paste", 1.0, "tbsp")
            ),
            instructions = "Cook it"
        )
        recipeRepository.insert(recipe)

        // Add 2 out of 3 ingredients to inventory
        inventoryRepository.add(
            InventoryItem(
                canonicalName = "chicken",
                displayName = "Chicken Breast",
                category = FoodCategory.PROTEIN,
                location = StorageLocation.FRIDGE,
                quantity = 2.0,
                unit = "lb"
            )
        )
        inventoryRepository.add(
            InventoryItem(
                canonicalName = "coconut_milk",
                displayName = "Coconut Milk",
                category = FoodCategory.CANNED,
                location = StorageLocation.PANTRY,
                quantity = 1.0,
                unit = "can"
            )
        )

        // Act
        val matches = recipeMatcherService.matchRecipes(textureFilter = emptySet())

        // Assert
        assertEquals(1, matches.size)
        val match = matches[0]
        assertEquals("Chicken Curry", match.recipe.title)
        assertEquals(2, match.matchedIngredients.size)
        assertEquals(1, match.missingIngredients.size)
        assertEquals("curry_paste", match.missingIngredients[0].canonicalName)

        // Check score logic:
        // matchedCount = 2, totalRequired = 3, missingCount = 1, expiringItemsUsed = 0
        // matchScore = (2/3 * 0.7) + (1.0 - 1/3) * 0.2 + (0/3 * 0.1)
        //            = 0.46666... + 0.13333... + 0 = 0.60
        assertEquals(0.6, match.matchScore, 0.001)
    }
}
