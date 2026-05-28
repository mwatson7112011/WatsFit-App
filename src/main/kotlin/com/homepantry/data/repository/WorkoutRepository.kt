package com.homepantry.data.repository

import com.homepantry.db.PantryDatabase
import com.homepantry.db.SelectAll
import com.homepantry.db.SelectRecent
import com.homepantry.domain.model.Difficulty
import com.homepantry.domain.model.Exercise
import com.homepantry.domain.model.MuscleGroup
import com.homepantry.domain.model.WorkoutExerciseEntry
import com.homepantry.domain.model.WorkoutLog
import com.homepantry.domain.model.WorkoutRoutine
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class WorkoutRepository(private val database: PantryDatabase) {

    private val exerciseQueries = database.exerciseQueries
    private val routineQueries = database.workoutRoutineQueries
    private val logQueries = database.workoutLogQueries
    private val json = Json { ignoreUnknownKeys = true }

    // ========== Exercises ==========

    fun getAllExercises(): List<Exercise> =
        exerciseQueries.selectAll().executeAsList().map { it.toDomain() }

    fun getExerciseById(id: Long): Exercise? =
        exerciseQueries.selectById(id).executeAsOneOrNull()?.toDomain()

    fun searchExercises(query: String): List<Exercise> =
        exerciseQueries.searchByName(query).executeAsList().map { it.toDomain() }

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

    // ========== Routines ==========

    fun getAllRoutines(): List<WorkoutRoutine> =
        routineQueries.selectAll().executeAsList().map { it.toDomain() }

    fun getRoutineById(id: Long): WorkoutRoutine? =
        routineQueries.selectById(id).executeAsOneOrNull()?.toDomain()

    fun getProgramByDifficulty(difficulty: String): List<WorkoutRoutine> =
        routineQueries.selectProgramByDifficulty(difficulty).executeAsList().map { it.toDomain() }

    fun getRoutineByDay(difficulty: String, day: Int): List<WorkoutRoutine> =
        routineQueries.selectByDay(difficulty, day.toLong()).executeAsList().map { it.toDomain() }

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

    // ========== Logs ==========

    fun getAllLogs(): List<WorkoutLog> =
        logQueries.selectAll().executeAsList().map { it.toDomain() }

    fun getRecentLogs(limit: Long): List<WorkoutLog> =
        logQueries.selectRecent(limit).executeAsList().map { it.toDomain() }

    fun logWorkout(routineId: Long, durationMin: Int?, notes: String?) {
        val now = Clock.System.now().toString()
        logQueries.insert(
            routineId = routineId,
            completedAt = now,
            durationMin = durationMin?.toLong(),
            notes = notes
        )
    }

    fun getWorkoutsThisWeek(weekStartDate: String): Long =
        logQueries.countThisWeek(weekStartDate).executeAsOne()

    // ========== Mapping helpers ==========

    private fun com.homepantry.db.Exercise.toDomain(): Exercise {
        val parsedMuscleGroups: List<MuscleGroup> = try {
            json.decodeFromString<List<String>>(muscleGroups).map { MuscleGroup.fromString(it) }
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
            difficulty = Difficulty.fromString(difficulty),
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
            difficulty = Difficulty.fromString(difficulty),
            durationMin = durationMin.toInt(),
            exercises = parsedExercises,
            dayOfProgram = dayOfProgram?.toInt(),
            tags = parsedTags
        )
    }

    private fun SelectAll.toDomain(): WorkoutLog =
        WorkoutLog(
            id = id,
            routineId = routineId,
            routineTitle = routineTitle ?: "",
            completedAt = completedAt,
            durationMin = durationMin?.toInt(),
            notes = notes
        )

    private fun SelectRecent.toDomain(): WorkoutLog =
        WorkoutLog(
            id = id,
            routineId = routineId,
            routineTitle = routineTitle ?: "",
            completedAt = completedAt,
            durationMin = durationMin?.toInt(),
            notes = notes
        )

    private fun com.homepantry.db.SelectProgramByDifficulty.toDomain(): WorkoutRoutine {
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
            difficulty = Difficulty.fromString(difficulty),
            durationMin = durationMin.toInt(),
            exercises = parsedExercises,
            dayOfProgram = dayOfProgram.toInt(),
            tags = parsedTags
        )
    }
}
