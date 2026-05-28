package com.homepantry.ui.recipe

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.homepantry.data.repository.InventoryRepository
import com.homepantry.domain.model.Cuisine
import com.homepantry.domain.model.Recipe
import com.homepantry.domain.model.RecipeIngredient
import com.homepantry.domain.model.RecipeMatch
import com.homepantry.domain.model.TextureCategory
import com.homepantry.data.repository.GroceryListRepository
import com.homepantry.data.repository.RecipeRepository
import com.homepantry.domain.model.GroceryListItem
import com.homepantry.domain.service.InventoryManager
import com.homepantry.domain.service.RecipeMatcherService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecipeState(
    val matches: List<RecipeMatch> = emptyList(),
    val searchQueries: String = "",
    val textureFilter: Set<TextureCategory> = setOf(TextureCategory.SOFT, TextureCategory.TENDER, TextureCategory.MODERATE),
    val cuisineFilter: Cuisine? = null,
    val selectedRecipe: Recipe? = null,
    val selectedRecipeMatch: RecipeMatch? = null
)

class RecipeScreenModel(
    private val recipeMatcherService: RecipeMatcherService,
    private val inventoryManager: InventoryManager,
    private val inventoryRepository: InventoryRepository,
    private val groceryListRepository: GroceryListRepository,
    private val recipeRepository: RecipeRepository
) : ScreenModel {

    private val _state = MutableStateFlow(RecipeState())
    val state: StateFlow<RecipeState> = _state.asStateFlow()

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        screenModelScope.launch {
            val currentState = _state.value
            var matches = recipeMatcherService.matchRecipes(
                textureFilter = currentState.textureFilter,
                cuisineFilter = currentState.cuisineFilter
            )

            if (currentState.searchQueries.isNotBlank()) {
                matches = matches.filter { match ->
                    match.recipe.title.contains(currentState.searchQueries, ignoreCase = true) ||
                            match.recipe.tags.any { it.contains(currentState.searchQueries, ignoreCase = true) }
                }
            }

            _state.value = currentState.copy(matches = matches)

            // Update selected recipe details if it's currently open
            currentState.selectedRecipe?.let { selected ->
                val updatedMatch = matches.firstOrNull { it.recipe.id == selected.id }
                if (updatedMatch != null) {
                    _state.value = _state.value.copy(
                        selectedRecipe = updatedMatch.recipe,
                        selectedRecipeMatch = updatedMatch
                    )
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQueries = query)
        loadRecipes()
    }

    fun setCuisineFilter(cuisine: Cuisine?) {
        _state.value = _state.value.copy(cuisineFilter = cuisine)
        loadRecipes()
    }

    fun toggleTextureFilter(texture: TextureCategory) {
        val current = _state.value.textureFilter
        val updated = if (texture in current) {
            current - texture
        } else {
            current + texture
        }
        _state.value = _state.value.copy(textureFilter = updated)
        loadRecipes()
    }

    fun selectRecipe(recipe: Recipe?) {
        if (recipe == null) {
            _state.value = _state.value.copy(selectedRecipe = null, selectedRecipeMatch = null)
        } else {
            val match = _state.value.matches.firstOrNull { it.recipe.id == recipe.id }
            _state.value = _state.value.copy(selectedRecipe = recipe, selectedRecipeMatch = match)
        }
    }

    fun cookRecipe(recipe: Recipe, addMissingToGroceryList: Boolean) {
        screenModelScope.launch {
            inventoryManager.cookRecipe(recipe, addMissingToGroceryList)
            loadRecipes()
        }
    }

    fun addIngredientsToGroceryList(recipe: Recipe) {
        screenModelScope.launch {
            recipe.ingredients.forEach { ingredient ->
                val canonicalName = ingredient.canonicalName
                groceryListRepository.add(
                    GroceryListItem(
                        canonicalName = canonicalName,
                        displayName = ingredient.displayName,
                        quantity = ingredient.quantity,
                        unit = ingredient.unit,
                        addedFrom = recipe.title
                    )
                )
            }
            loadRecipes()
        }
    }

    fun addRecipe(
        title: String,
        category: String,
        cuisine: Cuisine,
        texture: TextureCategory,
        tags: List<String>,
        ingredients: List<RecipeIngredient>,
        instructions: String,
        prepTimeMin: Int?,
        cookTimeMin: Int?,
        servings: Int,
        calories: Int?
    ) {
        screenModelScope.launch {
            val nutrition = calories?.let { com.homepantry.domain.model.NutritionEstimate(it, 0, 0, 0) }
            val newRecipe = Recipe(
                title = title,
                category = category,
                cuisine = cuisine,
                texture = texture,
                tags = tags,
                ingredients = ingredients,
                instructions = instructions,
                prepTimeMin = prepTimeMin,
                cookTimeMin = cookTimeMin,
                servings = servings,
                nutrition = nutrition,
                isUserAdded = true
            )
            recipeRepository.insert(newRecipe)
            loadRecipes()
        }
    }
}
