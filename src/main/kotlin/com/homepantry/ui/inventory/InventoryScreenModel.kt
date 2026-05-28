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
import kotlinx.datetime.LocalDate

enum class SortOption(val displayName: String) {
    NAME("Name"),
    EXPIRATION("Expiration Date"),
    CATEGORY("Category")
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
            val allItems = inventoryRepository.getAll()
            updateFilteredItems(allItems)
        }
    }

    fun deleteItem(id: Long) {
        screenModelScope.launch {
            inventoryRepository.delete(id)
            loadItems()
        }
    }

    fun addItem(
        displayName: String,
        category: FoodCategory,
        location: StorageLocation,
        quantity: Double,
        unit: String,
        expirationDate: String?,
        notes: String?
    ) {
        screenModelScope.launch {
            val canonicalName = displayName.lowercase().replace(" ", "_")
            val item = InventoryItem(
                canonicalName = canonicalName,
                displayName = displayName,
                category = category,
                location = location,
                quantity = quantity,
                unit = unit,
                expirationDate = expirationDate?.takeIf { it.isNotBlank() },
                notes = notes?.takeIf { it.isNotBlank() }
            )
            inventoryRepository.add(item)
            loadItems()
        }
    }

    fun updateItem(
        id: Long,
        displayName: String,
        category: FoodCategory,
        location: StorageLocation,
        quantity: Double,
        unit: String,
        expirationDate: String?,
        notes: String?
    ) {
        screenModelScope.launch {
            val canonicalName = displayName.lowercase().replace(" ", "_")
            val item = InventoryItem(
                id = id,
                canonicalName = canonicalName,
                displayName = displayName,
                category = category,
                location = location,
                quantity = quantity,
                unit = unit,
                expirationDate = expirationDate?.takeIf { it.isNotBlank() },
                notes = notes?.takeIf { it.isNotBlank() }
            )
            inventoryRepository.update(item)
            loadItems()
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

    private fun updateFilteredItems(allItems: List<InventoryItem>) {
        val currentState = _state.value
        var filtered = allItems.filter { it.location == currentState.selectedLocation }

        if (currentState.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.displayName.contains(currentState.searchQuery, ignoreCase = true) ||
                        it.category.displayName.contains(currentState.searchQuery, ignoreCase = true)
            }
        }

        filtered = when (currentState.sortBy) {
            SortOption.NAME -> filtered.sortedBy { it.displayName.lowercase() }
            SortOption.EXPIRATION -> filtered.sortedWith(compareBy<InventoryItem> { it.expirationDate == null }
                .thenBy { it.expirationDate })
            SortOption.CATEGORY -> filtered.sortedBy { it.category.displayName }
        }

        _state.value = currentState.copy(items = filtered)
    }
}
