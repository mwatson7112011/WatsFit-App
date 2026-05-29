package com.homepantry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.homepantry.data.repository.RecipeRepository
import com.homepantry.data.repository.WorkoutRepository
import com.homepantry.data.seed.loadSeedData
import com.homepantry.di.commonModule
import com.homepantry.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            startKoin {
                androidContext(this@MainActivity)
                modules(commonModule, platformModule)
            }
        } catch (e: Exception) { }

        val koin = GlobalContext.get()
        val recipeRepo = koin.get<RecipeRepository>()
        val workoutRepo = koin.get<WorkoutRepository>()
        
        loadSeedData(recipeRepo, workoutRepo) { fileName ->
            try {
                assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                null
            }
        }

        setContent {
            App()
        }
    }
}
