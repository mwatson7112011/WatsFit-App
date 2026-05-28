package com.homepantry.di

import com.homepantry.data.DatabaseFactory
import com.homepantry.data.repository.GroceryListRepository
import com.homepantry.data.repository.HealthRepository
import com.homepantry.data.repository.InventoryRepository
import com.homepantry.data.repository.RecipeRepository
import com.homepantry.data.repository.WorkoutRepository
import com.homepantry.domain.service.RecipeMatcherService
import org.koin.dsl.module

val appModule = module {
    single { DatabaseFactory.create() }
    single { InventoryRepository(get()) }
    single { RecipeRepository(get()) }
    single { GroceryListRepository(get()) }
    single { WorkoutRepository(get()) }
    single { HealthRepository(get()) }
    single { RecipeMatcherService(get(), get()) }
    single { com.homepantry.domain.service.InventoryManager(get(), get()) }
    factory { com.homepantry.ui.inventory.InventoryScreenModel(get()) }
    factory { com.homepantry.ui.recipe.RecipeScreenModel(get(), get(), get(), get(), get()) }
    factory { com.homepantry.ui.grocery.GroceryListScreenModel(get()) }
    factory { com.homepantry.ui.workout.WorkoutScreenModel(get()) }
    factory { com.homepantry.ui.progress.ProgressScreenModel(get()) }
}

