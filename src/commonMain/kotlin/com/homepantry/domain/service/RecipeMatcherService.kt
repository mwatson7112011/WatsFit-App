package com.homepantry.domain.service

import com.homepantry.data.repository.InventoryRepository
import com.homepantry.data.repository.RecipeRepository
import com.homepantry.domain.model.Cuisine
import com.homepantry.domain.model.InventoryItem
import com.homepantry.domain.model.RecipeIngredient
import com.homepantry.domain.model.TextureCategory
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

data class RecipeMatch(
    val recipe: com.homepantry.domain.model.Recipe,
    val matchedIngredients: List<RecipeIngredient>,
    val missingIngredients: List<RecipeIngredient>,
    val matchScore: Double,
    val expiringItemsUsed: Int = 0
) {
    val matchPercentage: Int
        get() = (matchScore * 100).toInt()
}

class RecipeMatcherService(
    private val inventoryRepository: InventoryRepository,
    private val recipeRepository: RecipeRepository
) {

    fun matchRecipes(
        textureFilter: Set<TextureCategory>,
        cuisineFilter: Cuisine? = null,
        tagFilter: String? = null
    ): List<RecipeMatch> {
        var recipes = recipeRepository.getAll()

        if (textureFilter.isNotEmpty()) {
            recipes = recipes.filter { it.texture in textureFilter }
        }

        if (cuisineFilter != null) {
            recipes = recipes.filter { it.cuisine == cuisineFilter }
        }

        if (tagFilter != null) {
            recipes = recipes.filter { recipe ->
                recipe.tags.any { it.equals(tagFilter, ignoreCase = true) }
            }
        }

        val inventory = inventoryRepository.getAll()

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
                val expDate = inventoryMatch.expirationDate
                if (expDate != null) {
                    try {
                        val parsedExpDate = LocalDate.parse(expDate)
                        if (parsedExpDate <= expirationThreshold) {
                            expiringItemsUsed++
                        }
                    } catch (_: Exception) {}
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
