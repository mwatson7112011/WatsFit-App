package com.homepantry.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.homepantry.domain.model.Difficulty
import com.homepantry.domain.model.Exercise
import com.homepantry.domain.model.MuscleGroup
import com.homepantry.domain.model.WorkoutRoutine

class WorkoutScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<WorkoutScreenModel>()
        val state by screenModel.state.collectAsState()

        var activeTab by remember { mutableStateOf(0) } // 0: Routine, 1: Library, 2: History
        var showLogConfirmDialog by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            screenModel.loadInitialData()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Muscle Toning",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Equipment-free bodyweight workout planner",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Program Difficulty Selection
                    var difficultyExpanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { difficultyExpanded = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = "Program")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Level: ${state.selectedDifficulty.displayName}")
                        }
                        DropdownMenu(
                            expanded = difficultyExpanded,
                            onDismissRequest = { difficultyExpanded = false }
                        ) {
                            Difficulty.entries.forEach { diff ->
                                DropdownMenuItem(
                                    text = { Text(diff.displayName) },
                                    onClick = {
                                        screenModel.setDifficulty(diff)
                                        difficultyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Today's Routine", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Exercise Library", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("Workout History", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tab Content
                when (activeTab) {
                    0 -> RoutineTabContent(
                        state = state,
                        onRoutineSelect = { screenModel.selectRoutine(it) },
                        onCompleteClick = { showLogConfirmDialog = true }
                    )
                    1 -> ExerciseLibraryContent(
                        state = state,
                        onSearchChange = { screenModel.setExerciseSearchQuery(it) },
                        onMuscleChange = { screenModel.setMuscleGroupFilter(it) }
                    )
                    2 -> WorkoutHistoryContent(state = state)
                }
            }

            if (showLogConfirmDialog && state.selectedRoutine != null) {
                LogWorkoutDialog(
                    routine = state.selectedRoutine!!,
                    onDismiss = { showLogConfirmDialog = false },
                    onConfirm = { duration, notes ->
                        screenModel.completeWorkout(state.selectedRoutine!!.id, duration, notes)
                        showLogConfirmDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun RoutineTabContent(
    state: WorkoutState,
    onRoutineSelect: (WorkoutRoutine) -> Unit,
    onCompleteClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        // Left Column: Routines List (Day 1 - Day 30) - Width: 35%
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.35f)
        ) {
            Text(
                text = "30-Day Program (Day ${state.currentDay})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.routines, key = { it.id }) { routine ->
                    val isToday = routine.dayOfProgram == state.currentDay
                    val isSelected = state.selectedRoutine?.id == routine.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRoutineSelect(routine) },
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isSelected -> MaterialTheme.colorScheme.surfaceVariant
                                isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = if (isToday) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Day ${routine.dayOfProgram ?: "?"}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${routine.durationMin} min",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = routine.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Right Column: Routine Details - Width: 65%
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.65f)
        ) {
            val routine = state.selectedRoutine
            if (routine == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Select a routine to see details")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = routine.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = routine.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(onClick = onCompleteClick) {
                        Icon(Icons.Default.Check, contentDescription = "Complete")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Complete Workout")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.selectedRoutineExercises) { exercise ->
                        val entry = routine.exercises.firstOrNull { it.exerciseId == exercise.id }
                        ExerciseDetailCard(exercise = exercise, entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseDetailCard(
    exercise: Exercise,
    entry: com.homepantry.domain.model.WorkoutExerciseEntry?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = exercise.description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Targets (Sets x Reps)
                val sets = entry?.sets ?: exercise.defaultSets
                val targetsText = if (exercise.isTimeBased) {
                    val secs = entry?.holdSeconds ?: exercise.defaultHoldSeconds ?: 30
                    "$sets Sets x ${secs}s Hold"
                } else {
                    val reps = entry?.reps ?: exercise.defaultReps ?: 10
                    "$sets Sets x $reps Reps"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = targetsText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Instructions
            Text(
                text = "Form Instructions:",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            exercise.instructions.forEachIndexed { i, step ->
                Text(
                    text = "${i + 1}. $step",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ExerciseLibraryContent(
    state: WorkoutState,
    onSearchChange: (String) -> Unit,
    onMuscleChange: (MuscleGroup?) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.exerciseSearchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search exercises...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )

            // Muscle Group Dropdown
            var muscleExpanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(
                    onClick = { muscleExpanded = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Muscle Filter")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(state.exerciseMuscleFilter?.displayName ?: "All Muscles")
                }
                DropdownMenu(
                    expanded = muscleExpanded,
                    onDismissRequest = { muscleExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Muscles") },
                        onClick = {
                            onMuscleChange(null)
                            muscleExpanded = false
                        }
                    )
                    MuscleGroup.entries.forEach { muscle ->
                        DropdownMenuItem(
                            text = { Text(muscle.displayName) },
                            onClick = {
                                onMuscleChange(muscle)
                                muscleExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.filteredExercises) { exercise ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Difficulty: ${exercise.difficulty.displayName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = exercise.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Text(
                            text = "Target Muscles: ${exercise.muscleGroups.joinToString { it.displayName }}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutHistoryContent(state: WorkoutState) {
    if (state.logs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No workouts completed yet! Keep up the good work.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.logs) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = log.routineTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Completed on: ${log.completedAt.take(10)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!log.notes.isNullOrBlank()) {
                                Text(
                                    text = "Notes: ${log.notes}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${log.durationMin ?: 30} mins",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogWorkoutDialog(
    routine: WorkoutRoutine,
    onDismiss: () -> Unit,
    onConfirm: (Int?, String?) -> Unit
) {
    var durationStr by remember { mutableStateOf(routine.durationMin.toString()) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Complete ${routine.title}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = durationStr,
                    onValueChange = { durationStr = it },
                    label = { Text("Completed Duration (Minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Workout Notes (optional)") },
                    placeholder = { Text("e.g. Felt great today, toned my abs!") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val duration = durationStr.toIntOrNull() ?: routine.durationMin
                    onConfirm(duration, notes.takeIf { it.isNotBlank() })
                }
            ) {
                Text("Complete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
