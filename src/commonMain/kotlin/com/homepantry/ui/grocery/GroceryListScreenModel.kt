package com.homepantry.ui.grocery

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.homepantry.data.repository.GroceryListRepository
import com.homepantry.data.repository.InventoryRepository
import com.homepantry.domain.model.FoodCategory
import com.homepantry.domain.model.GroceryListItem
import com.homepantry.domain.model.InventoryItem
import com.homepantry.domain.model.StorageLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GroceryListState(
    val items: List<GroceryListItem> = emptyList(),
    val isScanning: Boolean = false
)

class GroceryListScreenModel(
    private val groceryListRepository: GroceryListRepository,
    private val inventoryRepository: InventoryRepository
) : ScreenModel {

    private val _state = MutableStateFlow(GroceryListState())
    val state: StateFlow<GroceryListState> = _state.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        screenModelScope.launch {
            val allItems = groceryListRepository.getAll()
            _state.value = _state.value.copy(items = allItems.sortedWith(
                compareBy<GroceryListItem> { it.checked }.thenByDescending { it.createdAt }
            ))
        }
    }

    fun setScanning(scanning: Boolean) {
        _state.value = _state.value.copy(isScanning = scanning)
    }

    fun addItem(displayName: String, quantity: Double, unit: String) {
        screenModelScope.launch {
            val canonicalName = displayName.lowercase().replace(" ", "_")
            val item = GroceryListItem(
                canonicalName = canonicalName,
                displayName = displayName,
                quantity = quantity,
                unit = unit,
                addedFrom = "Manual"
            )
            groceryListRepository.add(item)
            loadItems()
        }
    }

    fun onBarcodeScanned(barcode: String) {
        screenModelScope.launch {
            // In a real app, we'd look up the barcode in a database or API
            // For this demo, let's assume we found a "Scanned Item"
            val itemName = "Scanned Item ($barcode)"
            val canonicalName = "scanned_$barcode"
            
            // 1. Check if it's in the grocery list
            val groceryItem = state.value.items.find { it.canonicalName == canonicalName || it.displayName.contains(barcode) }
            
            if (groceryItem != null) {
                // Mark as checked and move to inventory
                if (!groceryItem.checked) {
                    groceryListRepository.toggleChecked(groceryItem.id)
                }
                
                inventoryRepository.add(InventoryItem(
                    canonicalName = groceryItem.canonicalName,
                    displayName = groceryItem.displayName,
                    category = FoodCategory.OTHER,
                    location = StorageLocation.PANTRY,
                    quantity = groceryItem.quantity,
                    unit = groceryItem.unit
                ))
            } else {
                // Just add to inventory directly if not on list
                inventoryRepository.add(InventoryItem(
                    canonicalName = canonicalName,
                    displayName = itemName,
                    category = FoodCategory.OTHER,
                    location = StorageLocation.PANTRY,
                    quantity = 1.0,
                    unit = "piece"
                ))
            }
            
            setScanning(false)
            loadItems()
        }
    }

    fun toggleChecked(id: Long) {
        screenModelScope.launch {
            groceryListRepository.toggleChecked(id)
            loadItems()
        }
    }

    fun deleteItem(id: Long) {
        screenModelScope.launch {
            groceryListRepository.delete(id)
            loadItems()
        }
    }

    fun clearChecked() {
        screenModelScope.launch {
            groceryListRepository.deleteChecked()
            loadItems()
        }
    }

    // Platform-specific actions should be handled via an interface
    // but for now we'll just keep them as-is if possible or comment them out
    fun exportAndOpenShoppingList() {
        // Platform specific logic moved to desktopMain or handled via expect/actual
    }

    fun copyGroceryListToClipboard() {
        // Platform specific logic moved to desktopMain or handled via expect/actual
    }
}
