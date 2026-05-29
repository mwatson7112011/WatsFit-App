package com.homepantry.ui.workout

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.homepantry.data.repository.WorkoutRepository
import com.homepantry.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WorkoutState(
    val routines: List<WorkoutRoutine> = emptyList(),
    val logs: List<WorkoutLog> = emptyList(),
    val selectedRoutine: WorkoutRoutine? = null,
    val selectedRoutineExercises: List<Exercise> = emptyList()
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
            val logs = workoutRepository.getAllLogs()
            val allRoutines = workoutRepository.getAllRoutines()
            val sortedRoutines = allRoutines.sortedBy { it.dayOfProgram ?: 999 }
            
            _state.value = _state.value.copy(
                logs = logs,
                routines = sortedRoutines
            )
        }
    }

    fun selectRoutine(routine: WorkoutRoutine?) {
        screenModelScope.launch {
            if (routine == null) {
                _state.value = _state.value.copy(selectedRoutine = null, selectedRoutineExercises = emptyList())
            } else {
                val exercises = routine.exercises.mapNotNull { entry ->
                    workoutRepository.getExerciseById(entry.exerciseId)
                }
                _state.value = _state.value.copy(
                    selectedRoutine = routine,
                    selectedRoutineExercises = exercises
                )
            }
        }
    }

    fun completeWorkout(routineId: Long, durationMin: Int?, notes: String?) {
        screenModelScope.launch {
            workoutRepository.logWorkout(routineId, durationMin, notes)
            loadInitialData()
        }
    }
}
