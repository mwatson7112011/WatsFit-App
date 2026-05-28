package com.homepantry.data.repository

import com.homepantry.db.PantryDatabase
import com.homepantry.domain.model.Cuisine
import com.homepantry.domain.model.NutritionEstimate
import com.homepantry.domain.model.Recipe
import com.homepantry.domain.model.RecipeIngredient
import com.homepantry.domain.model.TextureCategory
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class RecipeRepository(private val database: PantryDatabase) {

    private val queries = database.recipeQueries
    private val json = Json { ignoreUnknownKeys = true }

    fun getAll(): List<Recipe> =
        queries.selectAll().executeAsList().map { it.toDomain() }

    fun getById(id: Long): Recipe? =
        queries.selectById(id).executeAsOneOrNull()?.toDomain()

    fun search(query: String): List<Recipe> =
        queries.searchByTitle(query).executeAsList().map { it.toDomain() }

    fun getFiltered(): List<Recipe> =
        queries.selectFiltered().executeAsList().map { it.toDomain() }

    fun insert(recipe: Recipe) {
        val tagsJson = json.encodeToString(recipe.tags)
        val ingredientsJson = json.encodeToString(recipe.ingredients)
        val nutritionJson = recipe.nutrition?.let {
            json.encodeToString(it)
        }
        queries.insert(
            title = recipe.title,
            category = recipe.category,
            cuisine = recipe.cuisine.name.lowercase(),
            texture = recipe.texture.name.lowercase(),
            tags = tagsJson,
            ingredients = ingredientsJson,
            instructions = recipe.instructions,
            prepTimeMin = recipe.prepTimeMin?.toLong(),
            cookTimeMin = recipe.cookTimeMin?.toLong(),
            servings = recipe.servings.toLong(),
            nutrition = nutritionJson,
            isUserAdded = if (recipe.isUserAdded) 1L else 0L
        )
    }

    fun getCount(): Long =
        queries.countAll().executeAsOne()

    private fun com.homepantry.db.Recipe.toDomain(): Recipe {
        val parsedTags: List<String> = try {
            json.decodeFromString<List<String>>(tags)
        } catch (_: Exception) {
            emptyList()
        }

        val parsedIngredients: List<RecipeIngredient> = try {
            json.decodeFromString<List<RecipeIngredient>>(ingredients)
        } catch (_: Exception) {
            emptyList()
        }

        val parsedNutrition: NutritionEstimate? = nutrition?.let {
            try {
                json.decodeFromString<NutritionEstimate>(it)
            } catch (_: Exception) {
                null
            }
        }

        return Recipe(
            id = id,
            title = title,
            category = category,
            cuisine = Cuisine.fromString(cuisine),
            texture = TextureCategory.fromString(texture),
            tags = parsedTags,
            ingredients = parsedIngredients,
            instructions = instructions,
            prepTimeMin = prepTimeMin?.toInt(),
            cookTimeMin = cookTimeMin?.toInt(),
            servings = servings.toInt(),
            nutrition = parsedNutrition,
            isUserAdded = isUserAdded != 0L
        )
    }
}
