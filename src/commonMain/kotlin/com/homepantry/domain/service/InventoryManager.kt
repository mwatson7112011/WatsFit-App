package com.homepantry.domain.service

import com.homepantry.data.repository.GroceryListRepository
import com.homepantry.data.repository.InventoryRepository
import com.homepantry.domain.model.GroceryListItem
import com.homepantry.domain.model.Recipe

class InventoryManager(
    private val inventoryRepository: InventoryRepository,
    private val groceryListRepository: GroceryListRepository
) {

    fun cookRecipe(recipe: Recipe, addMissingToGroceryList: Boolean) {
        val inventory = inventoryRepository.getAll()

        for (ingredient in recipe.ingredients) {
            val matchedItem = inventory.firstOrNull {
                it.canonicalName.equals(ingredient.canonicalName, ignoreCase = true)
            }

            if (matchedItem != null) {
                val newQty = matchedItem.quantity - ingredient.quantity
                if (newQty > 0) {
                    inventoryRepository.updateQuantity(matchedItem.id, newQty)
                } else {
                    inventoryRepository.delete(matchedItem.id)
                }
            } else if (addMissingToGroceryList && !ingredient.optional) {
                groceryListRepository.add(
                    GroceryListItem(
                        canonicalName = ingredient.canonicalName,
                        displayName = ingredient.displayName,
                        quantity = ingredient.quantity,
                        unit = ingredient.unit,
                        addedFrom = recipe.title
                    )
                )
            }
        }
    }
}
