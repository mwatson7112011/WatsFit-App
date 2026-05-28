package com.homepantry.domain.service

import com.homepantry.data.repository.InventoryRepository
import com.homepantry.data.repository.RecipeRepository
import com.homepantry.domain.model.Cuisine
import com.homepantry.domain.model.InventoryItem
import com.homepantry.domain.model.RecipeIngredient
import com.homepantry.domain.model.RecipeMatch
import com.homepantry.domain.model.TextureCategory
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

class RecipeMatcherService(
    private val inventoryRepository: InventoryRepository,
    private val recipeRepository: RecipeRepository
) {

    fun matchRecipes(
        textureFilter: Set<TextureCategory>,
        cuisineFilter: Cuisine? = null,
        tagFilter: String? = null
    ): List<RecipeMatch> {
        // 1. Get all recipes
        var recipes = recipeRepository.getAll()

        // 2. Filter by texture
        if (textureFilter.isNotEmpty()) {
            recipes = recipes.filter { it.texture in textureFilter }
        }

        // 3. Filter by cuisine
        if (cuisineFilter != null) {
            recipes = recipes.filter { it.cuisine == cuisineFilter }
        }

        // 4. Filter by tag
        if (tagFilter != null) {
            recipes = recipes.filter { recipe ->
                recipe.tags.any { it.equals(tagFilter, ignoreCase = true) }
            }
        }

        // 5. Get all inventory items
        val inventory = inventoryRepository.getAll()

        // 6. Calculate match scores
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val expirationThreshold = today.plus(3, DateTimeUnit.DAY)

        return recipes.map { recipe ->
            calculateMatch(recipe, inventory, today, expirationThreshold)
        }.sortedByDescending { it.matchScore }
    }

    private fun calculateMatch(
        recipe: com.homepantry.domain.model.Recipe,
        inventory: List<InventoryItem>,
        today: LocalDate,
        expirationThreshold: LocalDate
    ): RecipeMatch {
        val requiredIngredients = recipe.ingredients.filter { !it.optional }
        val totalRequired = requiredIngredients.size

        if (totalRequired == 0) {
            return RecipeMatch(
                recipe = recipe,
                matchedIngredients = emptyList(),
                missingIngredients = emptyList(),
                matchScore = 1.0,
                expiringItemsUsed = 0
            )
        }

        val matched = mutableListOf<RecipeIngredient>()
        val missing = mutableListOf<RecipeIngredient>()
        var expiringItemsUsed = 0

        for (ingredient in requiredIngredients) {
            val inventoryMatch = inventory.firstOrNull { item ->
                item.canonicalName.equals(ingredient.canonicalName, ignoreCase = true)
            }
            if (inventoryMatch != null) {
                matched.add(ingredient)
                // Check if this inventory item expires within 3 days
                val expDate = inventoryMatch.expirationDate
                if (expDate != null) {
                    try {
                        val parsedExpDate = LocalDate.parse(expDate)
                        if (parsedExpDate <= expirationThreshold) {
                            expiringItemsUsed++
                        }
                    } catch (_: Exception) {
                        // Unparseable date — skip expiration check
                    }
                }
            } else {
                missing.add(ingredient)
            }
        }

        val matchedCount = matched.size.toDouble()
        val missingCount = missing.size.toDouble()

        val matchScore = (matchedCount / totalRequired) * 0.7 +
                (1.0 - missingCount / totalRequired) * 0.2 +
                (expiringItemsUsed.toDouble() / totalRequired) * 0.1

        return RecipeMatch(
            recipe = recipe,
            matchedIngredients = matched,
            missingIngredients = missing,
            matchScore = matchScore,
            expiringItemsUsed = expiringItemsUsed
        )
    }
}
