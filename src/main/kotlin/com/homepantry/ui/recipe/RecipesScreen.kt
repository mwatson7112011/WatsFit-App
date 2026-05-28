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
import com.homepantry.domain.model.RecipeMatch
import com.homepantry.domain.model.TextureCategory
import com.homepantry.ui.theme.AlertCoral
import com.homepantry.ui.theme.SuccessGreen
import com.homepantry.ui.theme.WarningGold
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
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Column: Recipe List (Width: 40%)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.4f)
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Healthy Recipes",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Mediterranean & Thai matches from your pantry",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { showAddRecipeDialog = true }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Recipe", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search input
                    OutlinedTextField(
                        value = state.searchQueries,
                        onValueChange = { screenModel.setSearchQuery(it) },
                        placeholder = { Text("Search recipes/tags...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cuisine Filter Tabs
                    TabRow(
                        selectedTabIndex = when (state.cuisineFilter) {
                            null -> 0
                            Cuisine.MEDITERRANEAN -> 1
                            Cuisine.THAI -> 2
                        },
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Tab(
                            selected = state.cuisineFilter == null,
                            onClick = { screenModel.setCuisineFilter(null) },
                            text = { Text("All Cuisines", fontSize = 12.sp) }
                        )
                        Tab(
                            selected = state.cuisineFilter == Cuisine.MEDITERRANEAN,
                            onClick = { screenModel.setCuisineFilter(Cuisine.MEDITERRANEAN) },
                            text = { Text("Med 🫒", fontSize = 12.sp) }
                        )
                        Tab(
                            selected = state.cuisineFilter == Cuisine.THAI,
                            onClick = { screenModel.setCuisineFilter(Cuisine.THAI) },
                            text = { Text("Thai 🇹🇭", fontSize = 12.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Texture filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextureCategory.entries.forEach { category ->
                            val selected = category in state.textureFilter
                            FilterChip(
                                selected = selected,
                                onClick = { screenModel.toggleTextureFilter(category) },
                                label = { Text(category.displayName, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recipe Match Cards List
                    if (state.matches.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No recipes found matching current filters",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.matches, key = { it.recipe.id }) { match ->
                                RecipeCard(
                                    match = match,
                                    isSelected = state.selectedRecipe?.id == match.recipe.id,
                                    onClick = { screenModel.selectRecipe(match.recipe) }
                                )
                            }
                        }
                    }
                }

                // Vertical Divider
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // Right Column: Recipe Details (Width: 60%)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.6f)
                        .padding(24.dp)
                ) {
                    val recipe = state.selectedRecipe
                    val match = state.selectedRecipeMatch

                    if (recipe == null || match == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Restaurant,
                                    contentDescription = "Select Recipe",
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Select a recipe on the left to see details",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        RecipeDetailView(
                            recipe = recipe,
                            match = match,
                            onCookClick = { showCookConfirmDialog = true },
                            onAddToGroceryClick = {
                                screenModel.addIngredientsToGroceryList(recipe)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Ingredients added to your shopping list!")
                                }
                            }
                        )
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
                    onConfirm = { addMissingToGrocery ->
                        screenModel.cookRecipe(state.selectedRecipe!!, addMissingToGrocery)
                        showCookConfirmDialog = false
                    }
                )
            }

            if (showAddRecipeDialog) {
                AddRecipeDialog(
                    onDismiss = { showAddRecipeDialog = false },
                    onSave = { title, category, cuisine, texture, tags, ingredients, instructions, prep, cook, servings, calories ->
                        screenModel.addRecipe(
                            title = title,
                            category = category,
                            cuisine = cuisine,
                            texture = texture,
                            tags = tags,
                            ingredients = ingredients,
                            instructions = instructions,
                            prepTimeMin = prep,
                            cookTimeMin = cook,
                            servings = servings,
                            calories = calories
                        )
                        showAddRecipeDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Recipe '$title' added successfully!")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RecipeCard(
    match: RecipeMatch,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (match.recipe.cuisine == Cuisine.MEDITERRANEAN) "🫒 Mediterranean" else "🇹🇭 Thai",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                match.matchScore >= 0.9 -> SuccessGreen.copy(alpha = 0.2f)
                                match.matchScore >= 0.5 -> WarningGold.copy(alpha = 0.2f)
                                else -> AlertCoral.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${match.matchPercentage}% Match",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            match.matchScore >= 0.9 -> SuccessGreen
                            match.matchScore >= 0.5 -> WarningGold
                            else -> AlertCoral
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = match.recipe.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${match.recipe.totalTimeMin ?: 20} mins",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Texture: ${match.recipe.texture.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RecipeDetailView(
    recipe: Recipe,
    match: RecipeMatch,
    onCookClick: () -> Unit,
    onAddToGroceryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    SuggestionChip(onClick = {}, label = { Text(recipe.cuisine.displayName) })
                    SuggestionChip(onClick = {}, label = { Text("Texture: ${recipe.texture.displayName}") })
                    recipe.tags.take(2).forEach { tag ->
                        SuggestionChip(onClick = {}, label = { Text(tag) })
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAddToGroceryClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Add to Shopping List")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add to Shopping List")
                }

                Button(
                    onClick = onCookClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.LocalCafe, contentDescription = "Cook This")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cook This")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Info Cards (Time, Servings, Calories)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoBlock(
                icon = Icons.Default.Timer,
                title = "Total Time",
                value = "${recipe.totalTimeMin ?: 20} mins",
                modifier = Modifier.weight(1f)
            )
            InfoBlock(
                icon = Icons.Default.People,
                title = "Servings",
                value = "${recipe.servings} servings",
                modifier = Modifier.weight(1f)
            )
            if (recipe.nutrition != null) {
                InfoBlock(
                    icon = Icons.Default.LocalFireDepartment,
                    title = "Calories",
                    value = "${recipe.nutrition.calories} kcal",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ingredients Section
        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recipe.ingredients.forEach { ingredient ->
                    val isMatched = match.matchedIngredients.any { it.canonicalName == ingredient.canonicalName }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isMatched) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = if (isMatched) "Have" else "Missing",
                                tint = if (isMatched) SuccessGreen else AlertCoral,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = ingredient.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "${ingredient.quantity} ${ingredient.unit}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Instructions Section
        Text(
            text = "Instructions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Multi-line instructions parser
        val instructionsList = remember(recipe.instructions) {
            recipe.instructions.split("\n").filter { it.isNotBlank() }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            instructionsList.forEachIndexed { index, step ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = step.trim().removePrefix("${index + 1}.").trim(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoBlock(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun CookConfirmationDialog(
    recipe: Recipe,
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit
) {
    var addMissingToGrocery by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cook ${recipe.title}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Confirming this will deduct the required ingredients from your inventory.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { addMissingToGrocery = !addMissingToGrocery }
                ) {
                    Checkbox(
                        checked = addMissingToGrocery,
                        onCheckedChange = { addMissingToGrocery = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Automatically add missing ingredients to my grocery list",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(addMissingToGrocery) }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddRecipeDialog(
    onDismiss: () -> Unit,
    onSave: (
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
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var cuisine by remember { mutableStateOf(Cuisine.MEDITERRANEAN) }
    var texture by remember { mutableStateOf(TextureCategory.TENDER) }
    var category by remember { mutableStateOf("dinner") }
    var tagsStr by remember { mutableStateOf("") }
    
    val ingredients = remember { mutableStateListOf<RecipeIngredient>() }
    var ingName by remember { mutableStateOf("") }
    var ingQtyStr by remember { mutableStateOf("") }
    var ingUnit by remember { mutableStateOf("piece") }
    var ingOptional by remember { mutableStateOf(false) }

    var instructions by remember { mutableStateOf("") }
    var prepTimeStr by remember { mutableStateOf("") }
    var cookTimeStr by remember { mutableStateOf("") }
    var servingsStr by remember { mutableStateOf("2") }
    var caloriesStr by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Recipe", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .width(600.dp)
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Recipe Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cuisine", style = MaterialTheme.typography.bodySmall)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Cuisine.entries.forEach { c ->
                                FilterChip(
                                    selected = cuisine == c,
                                    onClick = { cuisine = c },
                                    label = { Text(c.displayName) }
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Texture", style = MaterialTheme.typography.bodySmall)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextureCategory.entries.forEach { t ->
                                FilterChip(
                                    selected = texture == t,
                                    onClick = { texture = t },
                                    label = { Text(t.displayName) }
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
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Meal Category (e.g. breakfast, soup, dinner)") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = tagsStr,
                        onValueChange = { tagsStr = it },
                        label = { Text("Tags (comma separated)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = prepTimeStr,
                        onValueChange = { prepTimeStr = it },
                        label = { Text("Prep Time (min)") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = cookTimeStr,
                        onValueChange = { cookTimeStr = it },
                        label = { Text("Cook Time (min)") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = servingsStr,
                        onValueChange = { servingsStr = it },
                        label = { Text("Servings") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = caloriesStr,
                        onValueChange = { caloriesStr = it },
                        label = { Text("Calories") },
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider()

                Text("Ingredients Builder", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                if (ingredients.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ingredients.forEachIndexed { idx, ing ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${ing.displayName}: ${ing.quantity} ${ing.unit}${if (ing.optional) " (Optional)" else ""}")
                                    IconButton(
                                        onClick = { ingredients.removeAt(idx) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ingName,
                        onValueChange = { ingName = it },
                        label = { Text("Name") },
                        modifier = Modifier.weight(1.8f)
                    )
                    OutlinedTextField(
                        value = ingQtyStr,
                        onValueChange = { ingQtyStr = it },
                        label = { Text("Qty") },
                        modifier = Modifier.weight(0.8f)
                    )
                    OutlinedTextField(
                        value = ingUnit,
                        onValueChange = { ingUnit = it },
                        label = { Text("Unit") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = {
                            val qty = ingQtyStr.toDoubleOrNull() ?: 1.0
                            val canonicalName = ingName.lowercase().replace(" ", "_").trim()
                            if (ingName.isNotBlank()) {
                                ingredients.add(
                                    RecipeIngredient(
                                        canonicalName = canonicalName,
                                        displayName = ingName.trim(),
                                        quantity = qty,
                                        unit = ingUnit.trim(),
                                        optional = ingOptional
                                    )
                                )
                                ingName = ""
                                ingQtyStr = ""
                                ingOptional = false
                            }
                        },
                        enabled = ingName.isNotBlank()
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Ingredient", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions (one step per line)") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 10
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val tags = tagsStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    val prep = prepTimeStr.toIntOrNull()
                    val cook = cookTimeStr.toIntOrNull()
                    val servings = servingsStr.toIntOrNull() ?: 2
                    val calories = caloriesStr.toIntOrNull()
                    onSave(
                        title.trim(),
                        category.trim().lowercase(),
                        cuisine,
                        texture,
                        tags,
                        ingredients.toList(),
                        instructions.trim(),
                        prep,
                        cook,
                        servings,
                        calories
                    )
                },
                enabled = title.isNotBlank() && instructions.isNotBlank() && ingredients.isNotEmpty()
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

