package com.homepantry.ui.grocery

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.homepantry.data.repository.GroceryListRepository
import com.homepantry.domain.model.GroceryListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GroceryListState(
    val items: List<GroceryListItem> = emptyList()
)

class GroceryListScreenModel(
    private val groceryListRepository: GroceryListRepository
) : ScreenModel {

    private val _state = MutableStateFlow(GroceryListState())
    val state: StateFlow<GroceryListState> = _state.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        screenModelScope.launch {
            val allItems = groceryListRepository.getAll()
            _state.value = GroceryListState(items = allItems.sortedWith(
                compareBy<GroceryListItem> { it.checked }.thenByDescending { it.createdAt }
            ))
        }
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

    fun exportAndOpenShoppingList() {
        screenModelScope.launch {
            val items = groceryListRepository.getUnchecked()
            if (items.isEmpty()) return@launch

            val desktopDir = java.io.File(System.getProperty("user.home"), "Desktop")
            val file = java.io.File(desktopDir, "HomePantry_Shopping_List.txt")
            
            try {
                file.printWriter().use { out ->
                    out.println("=========================================")
                    out.println("        HOME PANTRY SHOPPING LIST        ")
                    out.println("=========================================")
                    out.println("Date: ${kotlinx.datetime.Clock.System.now().toString().substring(0, 10)}")
                    out.println()
                    
                    items.forEach { item ->
                        val qtyStr = "${item.quantity} ${item.unit}"
                        out.printf("- [ ] %-25s (%s)\n", item.displayName, qtyStr)
                        if (!item.addedFrom.isNullOrBlank()) {
                            out.println("      (Needed for: ${item.addedFrom})")
                        }
                    }
                    out.println()
                    out.println("=========================================")
                }

                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun copyGroceryListToClipboard() {
        screenModelScope.launch {
            val items = groceryListRepository.getUnchecked()
            if (items.isEmpty()) return@launch

            val sb = StringBuilder()
            sb.append("HOME PANTRY SHOPPING LIST\n")
            sb.append("Date: ${kotlinx.datetime.Clock.System.now().toString().substring(0, 10)}\n\n")
            
            items.forEach { item ->
                val qtyStr = "${item.quantity} ${item.unit}"
                sb.append("- [ ] ${item.displayName} ($qtyStr)")
                if (!item.addedFrom.isNullOrBlank()) {
                    sb.append(" (Needed for: ${item.addedFrom})")
                }
                sb.append("\n")
            }

            try {
                val selection = java.awt.datatransfer.StringSelection(sb.toString())
                val clipboard = java.awt.Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(selection, selection)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
