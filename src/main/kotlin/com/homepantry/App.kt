package com.homepantry

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.homepantry.ui.grocery.GroceryListScreen
import com.homepantry.ui.inventory.InventoryScreen
import com.homepantry.ui.progress.ProgressScreen
import com.homepantry.ui.recipe.RecipesScreen
import com.homepantry.ui.theme.HomePantryTheme
import com.homepantry.ui.workout.WorkoutScreen
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        HomePantryTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                TabNavigator(
                    tab = InventoryTab,
                    tabDisposable = {
                        TabDisposable(
                            navigator = it,
                            tabs = listOf(InventoryTab, RecipesTab, GroceryListTab, WorkoutTab, ProgressTab)
                        )
                    }
                ) { tabNavigator ->
                    Row(modifier = Modifier.fillMaxSize()) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            header = {
                                Text(
                                    text = "💪 WatsFit",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        ) {
                            Spacer(modifier = Modifier.weight(1f))

                            val tabs = listOf(InventoryTab, RecipesTab, GroceryListTab, WorkoutTab, ProgressTab)
                            tabs.forEach { tab ->
                                NavigationRailItem(
                                    selected = tabNavigator.current == tab,
                                    onClick = { tabNavigator.current = tab },
                                    icon = {
                                        tab.options.icon?.let { painter ->
                                            Icon(
                                                painter = painter,
                                                contentDescription = tab.options.title
                                            )
                                        }
                                    },
                                    label = { Text(tab.options.title) }
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            CurrentTab()
                        }
                    }
                }
            }
        }
    }
}

object InventoryTab : Tab {
    private fun readResolve(): Any = InventoryTab
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Kitchen)
            return remember {
                TabOptions(
                    index = 0u,
                    title = "Inventory",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        InventoryScreen().Content()
    }
}

object RecipesTab : Tab {
    private fun readResolve(): Any = RecipesTab
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Restaurant)
            return remember {
                TabOptions(
                    index = 1u,
                    title = "Recipes",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        RecipesScreen().Content()
    }
}

object GroceryListTab : Tab {
    private fun readResolve(): Any = GroceryListTab
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.ShoppingCart)
            return remember {
                TabOptions(
                    index = 2u,
                    title = "Grocery List",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        GroceryListScreen().Content()
    }
}

object WorkoutTab : Tab {
    private fun readResolve(): Any = WorkoutTab
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.FitnessCenter)
            return remember {
                TabOptions(
                    index = 3u,
                    title = "Workouts",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        WorkoutScreen().Content()
    }
}

object ProgressTab : Tab {
    private fun readResolve(): Any = ProgressTab
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.TrendingUp)
            return remember {
                TabOptions(
                    index = 4u,
                    title = "Progress",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        ProgressScreen().Content()
    }
}
