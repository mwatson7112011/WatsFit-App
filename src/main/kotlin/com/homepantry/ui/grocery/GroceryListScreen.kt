package com.homepantry.ui.grocery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.homepantry.domain.model.GroceryListItem
import kotlinx.coroutines.launch

class GroceryListScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<GroceryListScreenModel>()
        val state by screenModel.state.collectAsState()

        var showAddDialog by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

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
                            text = "Grocery List",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Ingredients you need to buy",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (state.items.any { !it.checked }) {
                            OutlinedButton(
                                onClick = {
                                    screenModel.copyGroceryListToClipboard()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Shopping list copied to clipboard!")
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy List")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copy List")
                            }

                            OutlinedButton(
                                onClick = { screenModel.exportAndOpenShoppingList() }
                            ) {
                                Icon(Icons.Default.Print, contentDescription = "Print List")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Print List")
                            }
                        }

                        if (state.items.any { it.checked }) {
                            OutlinedButton(
                                onClick = { screenModel.clearChecked() }
                            ) {
                                Icon(Icons.Default.ClearAll, contentDescription = "Clear Checked")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Clear Checked")
                            }
                        }

                        Button(
                            onClick = { showAddDialog = true }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Item")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Item")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Grocery Items List
                if (state.items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Empty List",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Your grocery list is empty",
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
                            GroceryItemCard(
                                item = item,
                                onCheckedChange = { screenModel.toggleChecked(item.id) },
                                onDelete = { screenModel.deleteItem(item.id) }
                            )
                        }
                    }
                }
            }

            if (showAddDialog) {
                AddGroceryItemDialog(
                    onDismiss = { showAddDialog = false },
                    onConfirm = { name, qty, unit ->
                        screenModel.addItem(name, qty, unit)
                        showAddDialog = false
                    }
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun GroceryItemCard(
    item: GroceryListItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (item.checked) 
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f) 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = item.checked,
                    onCheckedChange = onCheckedChange
                )
                
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (item.checked) TextDecoration.LineThrough else null,
                        color = if (item.checked) 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${item.quantity} ${item.unit}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (item.checked) 
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) 
                            else 
                                MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        if (!item.addedFrom.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            SuggestionChip(
                                onClick = {},
                                label = { Text("From: ${item.addedFrom}", fontSize = 10.sp) },
                                modifier = Modifier.height(24.dp),
                                enabled = !item.checked
                            )
                        }
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun AddGroceryItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantityStr by remember { mutableStateOf("1.0") }
    var unit by remember { mutableStateOf("piece") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Grocery Item") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantityStr.toDoubleOrNull() ?: 1.0
                    onConfirm(name, qty, unit)
                },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
