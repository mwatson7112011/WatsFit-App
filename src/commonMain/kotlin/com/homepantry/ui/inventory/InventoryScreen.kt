package com.homepantry.ui.inventory

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
                            text = "Manage your ingredients",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            editingItem = null
                            showAddEditDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Item")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Search Bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { screenModel.setSearchQuery(it) },
                    placeholder = { Text("Search ingredients...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Location Sub-Tabs
                ScrollableTabRow(
                    selectedTabIndex = state.selectedLocation.ordinal,
                    containerColor = Color.Transparent,
                    edgePadding = 0.dp
                ) {
                    StorageLocation.entries.forEach { location ->
                        Tab(
                            selected = state.selectedLocation == location,
                            onClick = { screenModel.setSelectedLocation(location) },
                            text = { Text(location.displayName) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Items List
                LazyColumn(
                    modifier = Modifier.weight(1f),
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

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.displayName, fontWeight = FontWeight.Bold)
                Text("${item.quantity} ${item.unit}", color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Add Item" else "Edit Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = quantityStr, onValueChange = { quantityStr = it }, label = { Text("Quantity") })
                OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(name, category, location, quantityStr.toDoubleOrNull() ?: 1.0, unit, expirationDate, notes)
            }) { Text("Save") }
        }
    )
}
