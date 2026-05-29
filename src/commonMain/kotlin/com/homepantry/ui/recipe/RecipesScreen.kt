package com.homepantry.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.homepantry.domain.model.Cuisine
import com.homepantry.domain.model.Recipe
import com.homepantry.domain.model.RecipeIngredient
import com.homepantry.domain.service.RecipeMatch
import com.homepantry.domain.model.TextureCategory
import kotlinx.coroutines.launch

class RecipesScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<RecipeScreenModel>()
        val state by screenModel.state.collectAsState()

        var showCookConfirmDialog by remember { mutableStateOf(false) }
        var showAddRecipeDialog by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            screenModel.loadRecipes()
        }

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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Healthy Recipes",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = { showAddRecipeDialog = true }) {
                        Icon(Icons.Default.Add, "Add Recipe")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (state.selectedRecipe != null && state.selectedRecipeMatch != null) {
                    RecipeDetailView(
                        recipe = state.selectedRecipe!!,
                        match = state.selectedRecipeMatch!!,
                        onBack = { screenModel.selectRecipe(null) },
                        onCookClick = { showCookConfirmDialog = true },
                        onAddToGroceryClick = {
                            screenModel.addIngredientsToGroceryList(state.selectedRecipe!!)
                            scope.launch {
                                snackbarHostState.showSnackbar("Ingredients added to list!")
                            }
                        }
                    )
                } else {
                    ScrollableTabRow(
                        selectedTabIndex = if (state.cuisineFilter == null) 0 else state.cuisineFilter!!.ordinal + 1,
                        containerColor = Color.Transparent,
                        edgePadding = 0.dp
                    ) {
                        Tab(
                            selected = state.cuisineFilter == null,
                            onClick = { screenModel.setCuisineFilter(null) },
                            text = { Text("All", fontSize = 12.sp) }
                        )
                        Cuisine.entries.forEach { cuisine ->
                            Tab(
                                selected = state.cuisineFilter == cuisine,
                                onClick = { screenModel.setCuisineFilter(cuisine) },
                                text = { Text(cuisine.displayName, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.searchQueries,
                        onValueChange = { screenModel.setSearchQuery(it) },
                        placeholder = { Text("Search recipes...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.matches.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            Text("No recipes found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.matches, key = { it.recipe.id }) { match ->
                                RecipeCard(
                                    match = match,
                                    onClick = { screenModel.selectRecipe(match.recipe) }
                                )
                            }
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
            )

            if (showCookConfirmDialog && state.selectedRecipe != null) {
                CookConfirmationDialog(
                    recipe = state.selectedRecipe!!,
                    onDismiss = { showCookConfirmDialog = false },
                    onConfirm = { addMissing ->
                        screenModel.cookRecipe(state.selectedRecipe!!, addMissing)
                        showCookConfirmDialog = false
                    }
                )
            }

            if (showAddRecipeDialog) {
                AddRecipeDialog(
                    onDismiss = { showAddRecipeDialog = false },
                    onSave = { t, c, cu, tx, tg, ing, ins, p, co, s, cal ->
                        screenModel.addRecipe(t, c, cu, tx, tg, ing, ins, p, co, s, cal)
                        showAddRecipeDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun RecipeCard(
    match: RecipeMatch,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(match.recipe.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("${match.matchPercentage}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Text("${match.recipe.category} • ${match.recipe.cuisine.displayName}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun RecipeDetailView(
    recipe: Recipe,
    match: RecipeMatch,
    onBack: () -> Unit,
    onCookClick: () -> Unit,
    onAddToGroceryClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            Text(recipe.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        
        Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAddToGroceryClick) { Text("Get Ingredients") }
            Button(onClick = onCookClick, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("Cook Now") }
        }

        if (recipe.description.isNotEmpty()) {
            Text("Description", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            Text(recipe.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
        }

        Text("Ingredients", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        recipe.ingredients.forEach { ing ->
            val hasIt = match.matchedIngredients.any { it.canonicalName == ing.canonicalName }
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${if(hasIt) "✅" else "❌"} ${ing.displayName}")
                Text("${ing.quantity} ${ing.unit}")
            }
        }

        Text("Instructions", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Text(recipe.instructions, modifier = Modifier.padding(bottom = 24.dp))
    }
}

@Composable
fun CookConfirmationDialog(recipe: Recipe, onDismiss: () -> Unit, onConfirm: (Boolean) -> Unit) {
    var addMissing by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cook ${recipe.title}") },
        text = {
            Column {
                Text("Confirm to deduct ingredients from inventory.")
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { addMissing = !addMissing }) {
                    Checkbox(checked = addMissing, onCheckedChange = { addMissing = it })
                    Text("Add missing to grocery list")
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(addMissing) }) { Text("Confirm") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddRecipeDialog(onDismiss: () -> Unit, onSave: (String, String, Cuisine, TextureCategory, List<String>, List<RecipeIngredient>, String, Int?, Int?, Int, Int?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Recipe") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = instructions, onValueChange = { instructions = it }, label = { Text("Instructions") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(title, "Dinner", Cuisine.MEDITERRANEAN, TextureCategory.TENDER, emptyList(), emptyList(), instructions, 10, 20, 2, 500)
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
