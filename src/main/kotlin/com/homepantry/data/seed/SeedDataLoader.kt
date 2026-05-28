package com.homepantry.data.seed

import com.homepantry.data.repository.RecipeRepository
import com.homepantry.data.repository.WorkoutRepository
import com.homepantry.domain.model.Cuisine
import com.homepantry.domain.model.Difficulty
import com.homepantry.domain.model.Exercise
import com.homepantry.domain.model.MuscleGroup
import com.homepantry.domain.model.Recipe
import com.homepantry.domain.model.SeedRecipeFile
import com.homepantry.domain.model.SeedWorkoutFile
import com.homepantry.domain.model.TextureCategory
import com.homepantry.domain.model.WorkoutRoutine
import kotlinx.serialization.json.Json

private val seedJson = Json { ignoreUnknownKeys = true }

fun loadSeedData(recipeRepository: RecipeRepository, workoutRepository: WorkoutRepository) {
    loadRecipes(recipeRepository)
    loadWorkouts(workoutRepository)
}

private fun loadRecipes(repository: RecipeRepository) {
    if (repository.getCount() > 0) return

    val stream = object {}::class.java.getResourceAsStream("/seed_recipes.json") ?: run {
        println("Warning: seed_recipes.json not found on classpath")
        return
    }

    val content = stream.bufferedReader().use { it.readText() }
    val seedFile = seedJson.decodeFromString(SeedRecipeFile.serializer(), content)

    seedFile.recipes.forEach { seed ->
        val recipe = Recipe(
            title = seed.title,
            category = seed.category,
            cuisine = Cuisine.fromString(seed.cuisine),
            texture = TextureCategory.fromString(seed.texture),
            tags = seed.tags,
            ingredients = seed.ingredients,
            instructions = seed.instructions.joinToString("\n"),
            prepTimeMin = seed.prepTimeMin,
            cookTimeMin = seed.cookTimeMin,
            servings = seed.servings,
            nutrition = seed.nutrition,
            isUserAdded = false
        )
        repository.insert(recipe)
    }

    println("Loaded ${seedFile.recipes.size} seed recipes")
}

private fun loadWorkouts(repository: WorkoutRepository) {
    val loadExercises = repository.getExerciseCount() == 0L
    val loadRoutines = repository.getRoutineCount() == 0L

    if (!loadExercises && !loadRoutines) return

    val stream = object {}::class.java.getResourceAsStream("/seed_workouts.json") ?: run {
        println("Warning: seed_workouts.json not found on classpath")
        return
    }

    val content = stream.bufferedReader().use { it.readText() }
    val seedFile = seedJson.decodeFromString(SeedWorkoutFile.serializer(), content)

    if (loadExercises) {
        seedFile.exercises.forEach { seed ->
            val exercise = Exercise(
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
            )
            repository.insertExercise(exercise)
        }
        println("Loaded ${seedFile.exercises.size} seed exercises")
    }

    if (loadRoutines) {
        seedFile.routines.forEach { seed ->
            val routine = WorkoutRoutine(
                title = seed.title,
                description = seed.description,
                category = seed.category,
                difficulty = Difficulty.fromString(seed.difficulty),
                durationMin = seed.durationMin,
                exercises = seed.exercises,
                dayOfProgram = seed.dayOfProgram,
                tags = seed.tags
            )
            repository.insertRoutine(routine)
        }
        println("Loaded ${seedFile.routines.size} seed routines")
    }
}
