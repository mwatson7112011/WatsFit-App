package com.homepantry.data.seed

import com.homepantry.data.repository.RecipeRepository
import com.homepantry.data.repository.WorkoutRepository
import com.homepantry.domain.model.*
import kotlinx.serialization.json.Json

fun loadSeedData(recipeRepo: RecipeRepository, workoutRepo: WorkoutRepository, jsonLoader: (String) -> String?) {
    val json = Json { ignoreUnknownKeys = true }
    
    // Force load if the database is missing any of the major cuisine categories or total count is low
    val allRecipes = recipeRepo.getAll()
    val currentCuisines = allRecipes.map { it.cuisine }.toSet()
    val needsJapanese = !currentCuisines.contains(Cuisine.JAPANESE)
    val needsSushi = !currentCuisines.contains(Cuisine.SUSHI)
    
    if (allRecipes.size < 200 || needsJapanese || needsSushi) {
        val files = listOf(
            "seed_recipes.json", 
            "seed_recipes_thai.json", 
            "seed_recipes_mediterranean.json",
            "seed_recipes_mexican.json",
            "seed_recipes_russian.json",
            "seed_recipes_italian.json",
            "seed_recipes_japanese.json",
            "seed_recipes_sushi.json"
        )
        
        files.forEach { fileName ->
            val content = jsonLoader(fileName)
            if (content != null) {
                try {
                    val seedFile = json.decodeFromString<SeedRecipeFile>(content)
                    seedFile.recipes.forEach { seed ->
                        recipeRepo.insert(Recipe(
                            title = seed.title,
                            description = seed.description,
                            category = seed.category,
                            cuisine = Cuisine.fromString(seed.cuisine),
                            texture = TextureCategory.fromString(seed.texture),
                            tags = seed.tags,
                            ingredients = seed.ingredients,
                            instructions = seed.instructions.joinToString("\n"),
                            prepTimeMin = seed.prepTimeMin,
                            cookTimeMin = seed.cookTimeMin,
                            servings = seed.servings,
                            nutrition = seed.nutrition
                        ))
                    }
                } catch (e: Exception) {
                    println("Error loading $fileName: ${e.message}")
                }
            }
        }
    }

    if (workoutRepo.getRoutineCount() < 10L) {
        val content = jsonLoader("seed_workouts.json")
        if (content != null) {
            try {
                val seedFile = json.decodeFromString<SeedWorkoutFile>(content)
                seedFile.exercises.forEach { seed ->
                    workoutRepo.insertExercise(Exercise(
                        name = seed.name,
                        description = seed.description,
                        muscleGroups = seed.muscleGroups.map { MuscleGroup.fromString(it) },
                        difficulty = Difficulty.fromString(seed.difficulty),
                        instructions = seed.instructions,
                        defaultReps = seed.defaultReps,
                        defaultSets = seed.defaultSets,
                        defaultHoldSeconds = seed.defaultHoldSeconds,
                        restSeconds = seed.restSeconds,
                        isTimeBased = seed.isTimeBased
                    ))
                }
                
                seedFile.routines.forEach { seed ->
                    val difficulty = Difficulty.fromString(seed.difficulty)
                    val originalDay = seed.dayOfProgram ?: 1
                    val shiftedDay = when (difficulty) {
                        Difficulty.BEGINNER -> originalDay
                        Difficulty.INTERMEDIATE -> originalDay + 30
                        Difficulty.ADVANCED -> originalDay + 60
                    }

                    workoutRepo.insertRoutine(WorkoutRoutine(
                        title = seed.title,
                        description = seed.description,
                        category = seed.category,
                        difficulty = difficulty,
                        durationMin = seed.durationMin,
                        exercises = seed.exercises,
                        dayOfProgram = shiftedDay,
                        tags = seed.tags
                    ))
                }
            } catch (e: Exception) {
                println("Error loading workouts: ${e.message}")
            }
        }
    }
}
