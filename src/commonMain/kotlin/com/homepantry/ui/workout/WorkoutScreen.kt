package com.homepantry.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.homepantry.domain.model.Exercise
import com.homepantry.domain.model.WorkoutExerciseEntry
import com.homepantry.domain.model.WorkoutRoutine

class WorkoutScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<WorkoutScreenModel>()
        val state by screenModel.state.collectAsState()

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
                Text(
                    text = "90-Day Challenge",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (state.selectedRoutine != null) {
                    RoutineDetailView(
                        routine = state.selectedRoutine!!,
                        exercises = state.selectedRoutineExercises,
                        onBack = { screenModel.selectRoutine(null) },
                        onComplete = {
                            screenModel.completeWorkout(state.selectedRoutine!!.id, 30, "Completed on tablet")
                            screenModel.selectRoutine(null)
                        }
                    )
                } else {
                    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { Text("History", fontWeight = FontWeight.Bold) }
                        items(state.logs) { log ->
                            Text("${log.routineTitle} - ${log.completedAt.take(10)}")
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)); Text("Available Routines", fontWeight = FontWeight.Bold) }
                        items(state.routines) { routine ->
                            Card(modifier = Modifier.fillMaxWidth().clickable { screenModel.selectRoutine(routine) }) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Day ${routine.dayOfProgram}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text(routine.difficulty.displayName, style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(routine.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("${routine.durationMin} mins")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoutineDetailView(routine: WorkoutRoutine, exercises: List<Exercise>, onBack: () -> Unit, onComplete: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            Text(routine.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        
        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            item {
                Text(routine.description, modifier = Modifier.padding(vertical = 16.dp))
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                Text("Exercises (No Equipment Needed)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            items(exercises) { exercise ->
                val entry = routine.exercises.find { it.exerciseId == exercise.id }
                ExerciseCard(exercise, entry)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Text("Complete Workout")
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, entry: WorkoutExerciseEntry?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                // Display Sets and Reps/Hold
                val sets = entry?.sets ?: exercise.defaultSets
                val reps = entry?.reps ?: exercise.defaultReps ?: 10
                val hold = entry?.holdSeconds ?: exercise.defaultHoldSeconds ?: 30
                
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (exercise.isTimeBased) "$sets Sets x ${hold}s" else "$sets Sets x $reps Reps",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            
            Text("Instructions:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            exercise.instructions.forEachIndexed { index, step ->
                Text("${index + 1}. $step", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}
