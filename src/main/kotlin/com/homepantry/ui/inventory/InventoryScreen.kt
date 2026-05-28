package com.homepantry.ui.inventory

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.homepantry.domain.model.FoodCategory
import com.homepantry.domain.model.InventoryItem
import com.homepantry.domain.model.StorageLocation
import com.homepantry.ui.theme.AlertCoral
import com.homepantry.ui.theme.WarningGold
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

class InventoryScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<InventoryScreenModel>()
        val state by screenModel.state.collectAsState()

        var showAddEditDialog by remember { mutableStateOf(false) }
        var editingItem by remember { mutableStateOf<InventoryItem?>(null) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Food Inventory",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Manage your ingredients and track expiration dates",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            editingItem = null
                            showAddEditDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Item")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Search & Sort Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { screenModel.setSearchQuery(it) },
                        placeholder = { Text("Search ingredients...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Sort Dropdown
                    var sortExpanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { sortExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sort: ${state.sortBy.displayName}")
                        }
                        DropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false }
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayName) },
                                    onClick = {
                                        screenModel.setSortBy(option)
                                        sortExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Location Sub-Tabs
                TabRow(
                    selectedTabIndex = state.selectedLocation.ordinal,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StorageLocation.entries.forEach { location ->
                        Tab(
                            selected = state.selectedLocation == location,
                            onClick = { screenModel.setSelectedLocation(location) },
                            text = {
                                Text(
                                    text = location.displayName,
                                    fontWeight = if (state.selectedLocation == location) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Items List
                if (state.items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Inbox,
                                contentDescription = "Empty",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (state.searchQuery.isNotBlank()) "No matching items found" else "This location is empty",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items, key = { it.id }) { item ->
                            InventoryItemCard(
                                item = item,
                                onClick = {
                                    editingItem = item
                                    showAddEditDialog = true
                                },
                                onDelete = { screenModel.deleteItem(item.id) }
                            )
                        }
                    }
                }
            }

            if (showAddEditDialog) {
                AddEditItemDialog(
                    item = editingItem,
                    onDismiss = { showAddEditDialog = false },
                    onConfirm = { name, cat, loc, qty, unit, exp, notes ->
                        if (editingItem == null) {
                            screenModel.addItem(name, cat, loc, qty, unit, exp, notes)
                        } else {
                            screenModel.updateItem(editingItem!!.id, name, cat, loc, qty, unit, exp, notes)
                        }
                        showAddEditDialog = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InventoryItemCard(
    item: InventoryItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val threeDaysFromNow = today.plus(3, DateTimeUnit.DAY)
    val sevenDaysFromNow = today.plus(7, DateTimeUnit.DAY)

    val expStatusColor = remember(item.expirationDate) {
        val expDateStr = item.expirationDate ?: return@remember Color.Unspecified
        try {
            val expDate = LocalDate.parse(expDateStr)
            when {
                expDate <= today -> AlertCoral          // Expired
                expDate <= threeDaysFromNow -> AlertCoral // Expiring soon (3 days)
                expDate <= sevenDaysFromNow -> WarningGold // Expiring in a week
                else -> Color.Unspecified
            }
        } catch (_: Exception) {
            Color.Unspecified
        }
    }

    val expText = remember(item.expirationDate) {
        val expDateStr = item.expirationDate ?: return@remember null
        try {
            val expDate = LocalDate.parse(expDateStr)
            when {
                expDate < today -> "Expired ($expDateStr)"
                expDate == today -> "Expires today!"
                expDate <= threeDaysFromNow -> "Expires in ${(expDate.dayOfYear - today.dayOfYear)} days ($expDateStr)"
                else -> "Expires: $expDateStr"
            }
        } catch (_: Exception) {
            "Expires: $expDateStr"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SuggestionChip(
                        onClick = {},
                        label = { Text(item.category.displayName, fontSize = 10.sp) },
                        modifier = Modifier.height(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.quantity} ${item.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    if (expText != null) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = expText,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (expStatusColor != Color.Unspecified) expStatusColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (expStatusColor != Color.Unspecified) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                if (!item.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun AddEditItemDialog(
    item: InventoryItem?,
    onDismiss: () -> Unit,
    onConfirm: (String, FoodCategory, StorageLocation, Double, String, String?, String?) -> Unit
) {
    var name by remember { mutableStateOf(item?.displayName ?: "") }
    var category by remember { mutableStateOf(item?.category ?: FoodCategory.PRODUCE) }
    var location by remember { mutableStateOf(item?.location ?: StorageLocation.PANTRY) }
    var quantityStr by remember { mutableStateOf(item?.quantity?.toString() ?: "1.0") }
    var unit by remember { mutableStateOf(item?.unit ?: "g") }
    var expirationDate by remember { mutableStateOf(item?.expirationDate ?: "") }
    var notes by remember { mutableStateOf(item?.notes ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (item == null) "Add Inventory Item" else "Edit Inventory Item") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Category Selection
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = category.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                IconButton(onClick = { categoryExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Category")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            FoodCategory.entries.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.displayName) },
                                    onClick = {
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Location Selection
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = location.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Location") },
                            trailingIcon = {
                                IconButton(onClick = { locationExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Location")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = locationExpanded,
                            onDismissRequest = { locationExpanded = false }
                        ) {
                            StorageLocation.entries.forEach { loc ->
                                DropdownMenuItem(
                                    text = { Text(loc.displayName) },
                                    onClick = {
                                        location = loc
                                        locationExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (e.g. g, piece, cup)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = expirationDate,
                    onValueChange = { expirationDate = it },
                    label = { Text("Expiration Date (YYYY-MM-DD)") },
                    placeholder = { Text("e.g. 2026-06-30") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantityStr.toDoubleOrNull() ?: 1.0
                    onConfirm(
                        name,
                        category,
                        location,
                        qty,
                        unit,
                        expirationDate.takeIf { it.isNotBlank() },
                        notes.takeIf { it.isNotBlank() }
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Simple ScrollState helper for Dialog since Dialog doesn't have standard column scroll
@Composable
fun rememberScrollState() = androidx.compose.foundation.rememberScrollState()
