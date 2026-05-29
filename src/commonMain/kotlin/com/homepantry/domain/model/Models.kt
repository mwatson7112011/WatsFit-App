package com.homepantry.domain.model

import kotlinx.serialization.Serializable

enum class StorageLocation(val displayName: String) {
    PANTRY("Pantry"), FRIDGE("Fridge"), FREEZER("Freezer"), SPICE("Spices");
    companion object {
        fun fromString(value: String): StorageLocation = entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: PANTRY
    }
}

enum class FoodCategory(val displayName: String) {
    PRODUCE("Produce"), PROTEIN("Protein"), DAIRY("Dairy"), GRAIN("Grain"), CANNED("Canned"), CONDIMENT("Condiment"), SNACK("Snack"), BEVERAGE("Beverage"), SPICE("Spice"), OIL("Oil & Vinegar"), LEGUME("Legume"), OTHER("Other");
    companion object {
        fun fromString(value: String): FoodCategory = entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OTHER
    }
}

enum class TextureCategory(val displayName: String) {
    SOFT("Soft"), TENDER("Tender"), MODERATE("Moderate"), CRUNCHY("Crunchy");
    companion object {
        fun fromString(value: String): TextureCategory = entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: TENDER
    }
}

enum class Cuisine(val displayName: String) {
    MEDITERRANEAN("Mediterranean"),
    THAI("Thai"),
    MEXICAN("Mexican"),
    RUSSIAN("Russian"),
    ITALIAN("Italian"),
    JAPANESE("Japanese"),
    SUSHI("Sushi");

    companion object {
        fun fromString(value: String): Cuisine =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) } ?: MEDITERRANEAN
    }
}

@Serializable
data class RecipeIngredient(val canonicalName: String, val displayName: String, val quantity: Double, val unit: String, val optional: Boolean = false)

@Serializable
data class NutritionEstimate(val calories: Int, val proteinGrams: Int, val carbsGrams: Int, val fatGrams: Int, val fiberGrams: Int? = null, val sodiumMg: Int? = null)

data class InventoryItem(val id: Long = 0, val canonicalName: String, val displayName: String, val category: FoodCategory, val location: StorageLocation, val quantity: Double, val unit: String, val expirationDate: String? = null, val notes: String? = null, val createdAt: String = "", val updatedAt: String = "")

data class GroceryListItem(val id: Long = 0, val canonicalName: String, val displayName: String, val quantity: Double = 1.0, val unit: String = "piece", val checked: Boolean = false, val addedFrom: String? = null, val createdAt: String = "")

data class Recipe(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String,
    val cuisine: Cuisine = Cuisine.MEDITERRANEAN,
    val texture: TextureCategory = TextureCategory.TENDER,
    val tags: List<String> = emptyList(),
    val ingredients: List<RecipeIngredient> = emptyList(),
    val instructions: String = "",
    val prepTimeMin: Int? = null,
    val cookTimeMin: Int? = null,
    val servings: Int = 2,
    val nutrition: NutritionEstimate? = null,
    val isUserAdded: Boolean = false
)

// ========== WORKOUT MODELS ==========

enum class MuscleGroup(val displayName: String) {
    CHEST("Chest"), SHOULDERS("Shoulders"), TRICEPS("Triceps"), BACK("Back"), BICEPS("Biceps"), CORE("Core"), QUADS("Quads"), HAMSTRINGS("Hamstrings"), GLUTES("Glutes"), CALVES("Calves"), FULL_BODY("Full Body"), CARDIO("Cardio"), FLEXIBILITY("Flexibility");
    companion object {
        fun fromString(value: String): MuscleGroup = entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: FULL_BODY
    }
}

enum class Difficulty(val displayName: String) {
    BEGINNER("Beginner"), INTERMEDIATE("Intermediate"), ADVANCED("Advanced");
    companion object {
        fun fromString(value: String): Difficulty = entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: BEGINNER
    }
}

data class Exercise(val id: Long = 0, val name: String, val description: String, val muscleGroups: List<MuscleGroup>, val difficulty: Difficulty = Difficulty.BEGINNER, val instructions: List<String>, val defaultReps: Int? = null, val defaultSets: Int = 3, val defaultHoldSeconds: Int? = null, val restSeconds: Int = 30, val isTimeBased: Boolean = false)

@Serializable
data class WorkoutExerciseEntry(val exerciseId: Long, val exerciseName: String = "", val reps: Int? = null, val sets: Int = 3, val holdSeconds: Int? = null)

data class WorkoutRoutine(val id: Long = 0, val title: String, val description: String, val category: String, val difficulty: Difficulty = Difficulty.BEGINNER, val durationMin: Int = 30, val exercises: List<WorkoutExerciseEntry> = emptyList(), val dayOfProgram: Int? = null, val tags: List<String> = emptyList())

data class WorkoutLog(val id: Long = 0, val routineId: Long, val routineTitle: String = "", val completedAt: String, val durationMin: Int? = null, val notes: String? = null)

data class WeightLog(val id: Long = 0, val date: String, val weightLbs: Double, val notes: String? = null, val createdAt: String = "")

enum class HeartRateContext(val displayName: String) {
    RESTING("Resting"), POST_WORKOUT("Post-Workout"), MORNING("Morning"), ACTIVE("Active"), RECOVERY("Recovery");
    companion object {
        fun fromString(value: String): HeartRateContext = entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: RESTING
    }
}

data class HeartRateLog(val id: Long = 0, val date: String, val time: String, val bpm: Int, val context: HeartRateContext = HeartRateContext.RESTING, val notes: String? = null, val createdAt: String = "")

// ========== SEED DATA DTOs ==========

@Serializable
data class SeedRecipe(
    val id: String,
    val title: String,
    val description: String = "",
    val category: String,
    val cuisine: String,
    val texture: String,
    val prepTimeMin: Int = 0,
    val cookTimeMin: Int = 0,
    val servings: Int = 2,
    val tags: List<String> = emptyList(),
    val ingredients: List<RecipeIngredient> = emptyList(),
    val instructions: List<String> = emptyList(),
    val nutrition: NutritionEstimate? = null
)

@Serializable
data class SeedRecipeFile(val recipes: List<SeedRecipe>)

@Serializable
data class SeedExercise(val id: String, val name: String, val description: String, val muscleGroups: List<String>, val difficulty: String = "beginner", val instructions: List<String>, val defaultReps: Int? = null, val defaultSets: Int = 3, val defaultHoldSeconds: Int? = null, val restSeconds: Int = 30, val isTimeBased: Boolean = false)

@Serializable
data class SeedWorkoutRoutine(val id: String, val title: String, val description: String, val category: String, val difficulty: String = "beginner", val durationMin: Int = 30, val exercises: List<WorkoutExerciseEntry>, val dayOfProgram: Int? = null, val tags: List<String> = emptyList())

@Serializable
data class SeedWorkoutFile(val exercises: List<SeedExercise>, val routines: List<SeedWorkoutRoutine>)
