package com.homepantry.data.repository

import com.homepantry.db.PantryDatabase
import com.homepantry.domain.model.Difficulty
import com.homepantry.domain.model.Exercise
import com.homepantry.domain.model.MuscleGroup
import com.homepantry.domain.model.WorkoutExerciseEntry
import com.homepantry.domain.model.WorkoutLog
import com.homepantry.domain.model.WorkoutRoutine
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class WorkoutRepository(private val database: PantryDatabase) {

    private val exerciseQueries = database.exerciseQueries
    private val routineQueries = database.workoutRoutineQueries
    private val logQueries = database.workoutLogQueries
    private val json = Json { ignoreUnknownKeys = true }

    fun getAllExercises(): List<Exercise> =
        exerciseQueries.selectAll().executeAsList().map { it.toDomain() }

    fun getExerciseById(id: Long): Exercise? =
        exerciseQueries.selectById(id).executeAsOneOrNull()?.toDomain()

    fun insertExercise(exercise: Exercise) {
        val muscleGroupsJson = json.encodeToString(exercise.muscleGroups.map { it.name })
        val instructionsJson = json.encodeToString(exercise.instructions)
        exerciseQueries.insert(
            name = exercise.name,
            description = exercise.description,
            muscleGroups = muscleGroupsJson,
            difficulty = exercise.difficulty.name.lowercase(),
            instructions = instructionsJson,
            defaultReps = exercise.defaultReps?.toLong(),
            defaultSets = exercise.defaultSets.toLong(),
            defaultHoldSeconds = exercise.defaultHoldSeconds?.toLong(),
            restSeconds = exercise.restSeconds.toLong(),
            isTimeBased = if (exercise.isTimeBased) 1L else 0L
        )
    }

    fun getExerciseCount(): Long =
        exerciseQueries.countAll().executeAsOne()

    fun getAllRoutines(): List<WorkoutRoutine> =
        routineQueries.selectAll().executeAsList().map { it.toDomain() }

    fun getRoutineById(id: Long): WorkoutRoutine? =
        routineQueries.selectById(id).executeAsOneOrNull()?.toDomain()

    fun insertRoutine(routine: WorkoutRoutine) {
        val exercisesJson = json.encodeToString(routine.exercises)
        val tagsJson = json.encodeToString(routine.tags)
        routineQueries.insert(
            title = routine.title,
            description = routine.description,
            category = routine.category,
            difficulty = routine.difficulty.name.lowercase(),
            durationMin = routine.durationMin.toLong(),
            exercises = exercisesJson,
            dayOfProgram = routine.dayOfProgram?.toLong(),
            tags = tagsJson
        )
    }

    fun getRoutineCount(): Long =
        routineQueries.countAll().executeAsOne()

    fun getAllLogs(): List<WorkoutLog> =
        logQueries.selectAll().executeAsList().map { it.toDomain() }

    fun logWorkout(routineId: Long, durationMin: Int?, notes: String?) {
        val now = Clock.System.now().toString()
        logQueries.insert(
            routineId = routineId,
            completedAt = now,
            durationMin = durationMin?.toLong(),
            notes = notes
        )
    }

    private fun com.homepantry.db.Exercise.toDomain(): Exercise {
        val parsedMuscleGroups: List<MuscleGroup> = try {
            json.decodeFromString<List<String>>(muscleGroups).map { MuscleGroup.valueOf(it) }
        } catch (_: Exception) {
            emptyList()
        }

        val parsedInstructions: List<String> = try {
            json.decodeFromString<List<String>>(instructions)
        } catch (_: Exception) {
            emptyList()
        }

        return Exercise(
            id = id,
            name = name,
            description = description,
            muscleGroups = parsedMuscleGroups,
            difficulty = Difficulty.valueOf(difficulty.uppercase()),
            instructions = parsedInstructions,
            defaultReps = defaultReps?.toInt(),
            defaultSets = defaultSets.toInt(),
            defaultHoldSeconds = defaultHoldSeconds?.toInt(),
            restSeconds = restSeconds.toInt(),
            isTimeBased = isTimeBased != 0L
        )
    }

    private fun com.homepantry.db.WorkoutRoutine.toDomain(): WorkoutRoutine {
        val parsedExercises: List<WorkoutExerciseEntry> = try {
            json.decodeFromString<List<WorkoutExerciseEntry>>(exercises)
        } catch (_: Exception) {
            emptyList()
        }

        val parsedTags: List<String> = try {
            json.decodeFromString<List<String>>(tags)
        } catch (_: Exception) {
            emptyList()
        }

        return WorkoutRoutine(
            id = id,
            title = title,
            description = description,
            category = category,
            difficulty = Difficulty.valueOf(difficulty.uppercase()),
            durationMin = durationMin.toInt(),
            exercises = parsedExercises,
            dayOfProgram = dayOfProgram?.toInt(),
            tags = parsedTags
        )
    }

    private fun com.homepantry.db.SelectAll.toDomain(): WorkoutLog =
        WorkoutLog(
            id = id,
            routineId = routineId,
            routineTitle = routineTitle ?: "",
            completedAt = completedAt,
            durationMin = durationMin?.toInt(),
            notes = notes
        )
}
