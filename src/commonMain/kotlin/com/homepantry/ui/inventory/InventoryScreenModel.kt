package com.homepantry.ui.inventory

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.homepantry.data.repository.InventoryRepository
import com.homepantry.domain.model.FoodCategory
import com.homepantry.domain.model.InventoryItem
import com.homepantry.domain.model.StorageLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SortOption(val displayName: String) {
    NAME("Name"),
    EXPIRATION("Expiration"),
    CATEGORY("Category"),
    QUANTITY("Quantity")
}

data class InventoryState(
    val items: List<InventoryItem> = emptyList(),
    val searchQuery: String = "",
    val selectedLocation: StorageLocation = StorageLocation.PANTRY,
    val sortBy: SortOption = SortOption.NAME
)

class InventoryScreenModel(
    private val inventoryRepository: InventoryRepository
) : ScreenModel {

    private val _state = MutableStateFlow(InventoryState())
    val state: StateFlow<InventoryState> = _state.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        screenModelScope.launch {
            val items = if (state.value.searchQuery.isNotBlank()) {
                inventoryRepository.search(state.value.searchQuery)
            } else {
                inventoryRepository.getByLocation(state.value.selectedLocation.name)
            }

            val sortedItems = when (state.value.sortBy) {
                SortOption.NAME -> items.sortedBy { it.displayName }
                SortOption.EXPIRATION -> items.sortedBy { it.expirationDate ?: "9999-12-31" }
                SortOption.CATEGORY -> items.sortedBy { it.category.name }
                SortOption.QUANTITY -> items.sortedByDescending { it.quantity }
            }

            _state.value = _state.value.copy(items = sortedItems)
        }
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        loadItems()
    }

    fun setSelectedLocation(location: StorageLocation) {
        _state.value = _state.value.copy(selectedLocation = location)
        loadItems()
    }

    fun setSortBy(sortOption: SortOption) {
        _state.value = _state.value.copy(sortBy = sortOption)
        loadItems()
    }

    fun addItem(name: String, cat: FoodCategory, loc: StorageLocation, qty: Double, unit: String, exp: String?, notes: String?) {
        screenModelScope.launch {
            val item = InventoryItem(
                canonicalName = name.lowercase().replace(" ", "_"),
                displayName = name,
                category = cat,
                location = loc,
                quantity = qty,
                unit = unit,
                expirationDate = exp,
                notes = notes
            )
            inventoryRepository.add(item)
            loadItems()
        }
    }

    fun updateItem(id: Long, name: String, cat: FoodCategory, loc: StorageLocation, qty: Double, unit: String, exp: String?, notes: String?) {
        screenModelScope.launch {
            val item = InventoryItem(
                id = id,
                canonicalName = name.lowercase().replace(" ", "_"),
                displayName = name,
                category = cat,
                location = loc,
                quantity = qty,
                unit = unit,
                expirationDate = exp,
                notes = notes
            )
            inventoryRepository.update(item)
            loadItems()
        }
    }

    fun deleteItem(id: Long) {
        screenModelScope.launch {
            inventoryRepository.delete(id)
            loadItems()
        }
    }
}
