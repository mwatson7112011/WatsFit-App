package com.homepantry.ui.workout

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.homepantry.data.repository.WorkoutRepository
import com.homepantry.domain.model.Difficulty
import com.homepantry.domain.model.Exercise
import com.homepantry.domain.model.MuscleGroup
import com.homepantry.domain.model.WorkoutLog
import com.homepantry.domain.model.WorkoutRoutine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WorkoutState(
    val routines: List<WorkoutRoutine> = emptyList(),
    val selectedDifficulty: Difficulty = Difficulty.BEGINNER,
    val currentDay: Int = 1,
    val selectedRoutine: WorkoutRoutine? = null,
    val selectedRoutineExercises: List<Exercise> = emptyList(),
    val logs: List<WorkoutLog> = emptyList(),
    val allExercises: List<Exercise> = emptyList(),
    val exerciseSearchQuery: String = "",
    val exerciseMuscleFilter: MuscleGroup? = null,
    val filteredExercises: List<Exercise> = emptyList()
)

class WorkoutScreenModel(
    private val workoutRepository: WorkoutRepository
) : ScreenModel {

    private val _state = MutableStateFlow(WorkoutState())
    val state: StateFlow<WorkoutState> = _state.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        screenModelScope.launch {
            val logs = workoutRepository.getRecentLogs(20)
            val allExercises = workoutRepository.getAllExercises()
            
            // Determine current day based on logs of the current selected difficulty
            val currentDifficulty = _state.value.selectedDifficulty
            val lastLogForDifficulty = logs.firstOrNull { log ->
                val routine = workoutRepository.getRoutineById(log.routineId)
                routine?.difficulty == currentDifficulty && routine.dayOfProgram != null
            }
            
            val lastDay = lastLogForDifficulty?.let { log ->
                workoutRepository.getRoutineById(log.routineId)?.dayOfProgram
            } ?: 0
            
            val currentDay = if (lastDay >= 30) 1 else lastDay + 1
            val routines = workoutRepository.getProgramByDifficulty(currentDifficulty.name.lowercase())
            val todayRoutine = routines.firstOrNull { it.dayOfProgram == currentDay }

            _state.value = _state.value.copy(
                routines = routines,
                currentDay = currentDay,
                selectedRoutine = todayRoutine,
                logs = logs,
                allExercises = allExercises,
                filteredExercises = allExercises
            )
            
            todayRoutine?.let { selectRoutine(it) }
        }
    }

    fun setDifficulty(difficulty: Difficulty) {
        _state.value = _state.value.copy(selectedDifficulty = difficulty)
        loadInitialData()
    }

    fun selectRoutine(routine: WorkoutRoutine) {
        screenModelScope.launch {
            val exercises = routine.exercises.mapNotNull { entry ->
                workoutRepository.getExerciseById(entry.exerciseId)
            }
            _state.value = _state.value.copy(
                selectedRoutine = routine,
                selectedRoutineExercises = exercises
            )
        }
    }

    fun completeWorkout(routineId: Long, durationMin: Int?, notes: String?) {
        screenModelScope.launch {
            workoutRepository.logWorkout(routineId, durationMin, notes)
            loadInitialData()
        }
    }

    fun setExerciseSearchQuery(query: String) {
        _state.value = _state.value.copy(exerciseSearchQuery = query)
        filterExercises()
    }

    fun setMuscleGroupFilter(muscleGroup: MuscleGroup?) {
        _state.value = _state.value.copy(exerciseMuscleFilter = muscleGroup)
        filterExercises()
    }

    private fun filterExercises() {
        val currentState = _state.value
        var filtered = currentState.allExercises

        if (currentState.exerciseSearchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(currentState.exerciseSearchQuery, ignoreCase = true) ||
                        it.description.contains(currentState.exerciseSearchQuery, ignoreCase = true)
            }
        }

        if (currentState.exerciseMuscleFilter != null) {
            filtered = filtered.filter {
                currentState.exerciseMuscleFilter in it.muscleGroups
            }
        }

        _state.value = currentState.copy(filteredExercises = filtered)
    }
}
