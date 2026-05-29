package com.homepantry.di

import com.homepantry.data.repository.*
import com.homepantry.domain.service.*
import com.homepantry.ui.inventory.InventoryScreenModel
import com.homepantry.ui.recipe.RecipeScreenModel
import com.homepantry.ui.grocery.GroceryListScreenModel
import com.homepantry.ui.workout.WorkoutScreenModel
import com.homepantry.ui.progress.ProgressScreenModel
import org.koin.dsl.module

val commonModule = module {
    single { InventoryRepository(get()) }
    single { RecipeRepository(get()) }
    single { GroceryListRepository(get()) }
    single { WorkoutRepository(get()) }
    single { HealthRepository(get()) }
    single { RecipeMatcherService(get(), get()) }
    single { InventoryManager(get(), get()) }
    factory { InventoryScreenModel(get()) }
    factory { RecipeScreenModel(get(), get(), get(), get(), get()) }
    factory { GroceryListScreenModel(get(), get()) }
    factory { WorkoutScreenModel(get()) }
    factory { ProgressScreenModel(get()) }
}

expect val platformModule: org.koin.core.module.Module
